package com.mytripquest.domain.item.service;

import com.mytripquest.domain.item.dto.ItemDto;
import com.mytripquest.domain.item.dto.ShopItemDto;
import com.mytripquest.domain.item.dto.ShopItemResponseDto;
import com.mytripquest.domain.item.entity.Item;
import com.mytripquest.domain.item.entity.UserItem;

import com.mytripquest.domain.item.repository.ItemRepository;
import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<ItemDto> findAllItems() {
        return itemRepository.findAllItems().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ItemDto convertToDto(Item item) {
        return new ItemDto(
                item.getItemId(),
                item.getName(),
                item.getSlot() != null ? item.getSlot().name() : null,
                item.getImageUrl(),
                item.isPurchasable(),
                item.getPrice(),
                item.getCreatedAt());
    }

    public List<UserItem> findMyItems(Long userId) {
        return itemRepository.findUserItemsByUserId(userId);
    }

    // [수정] category 파라미터 추가
    @Transactional(readOnly = true)
    public ShopItemResponseDto getShopItems(Long userId, int page, int size, String category) {

        // 카테고리 필터링 조건 설정 ('all' 이거나 null이면 필터 없음)
        String searchCategory = (category == null || category.equals("all") || category.isEmpty()) ? null : category;

        // 1. Get total number of items (searchCategory 조건 추가)
        // 매퍼 메서드 이름도 명확하게 변경하거나, 기존 메서드에 파라미터 추가 필요
        long totalItems = itemRepository.countShopItems(searchCategory);
        int totalPages = (int) Math.ceil((double) totalItems / size);

        // 2. Get paginated items from the database (searchCategory 조건 추가)
        int offset = page * size;
        List<Item> paginatedItems = itemRepository.findShopItemsWithPagination(size, offset, searchCategory);

        // 3. Get the items the user already owns
        Set<Long> myItemIds;
        if (userId != null) {
            List<UserItem> myItems = findMyItems(userId);
            myItemIds = myItems.stream()
                    .map(UserItem::getItemId)
                    .collect(Collectors.toSet());
        } else {
            myItemIds = Collections.emptySet();
        }

        // 4. Combine the info to create ShopItemDto list
        List<ShopItemDto> shopItems = paginatedItems.stream()
                .map(item -> ShopItemDto.builder()
                        .id(item.getItemId())
                        .name(item.getName())
                        // Enum 타입을 소문자 문자열로 변환 (null safe)
                        .category(item.getSlot() != null ? item.getSlot().name() : "ETC")
                        .price(item.getPrice())
                        .imageUrl(item.getImageUrl())
                        .owned(myItemIds.contains(item.getItemId()))
                        .build())
                .collect(Collectors.toList());

        // 5. Build and return the response DTO
        return ShopItemResponseDto.builder()
                .items(shopItems)
                .currentPage(page)
                .totalPages(totalPages)
                .totalItems(totalItems)
                .build();
    }

    @Transactional
    public void buyItem(Long userId, Long itemId) {
        // 1. 아이템 정보 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));

        // 2. 사용자 정보 조회
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 3. 구매 가능 여부 확인
        if (!item.isPurchasable()) {
            throw new BusinessException(ErrorCode.ITEM_NOT_PURCHASABLE);
        }

        // 4. 이미 소유하고 있는지 확인
        itemRepository.findUserItem(userId, itemId).ifPresent(userItem -> {
            throw new BusinessException(ErrorCode.ITEM_ALREADY_OWNED);
        });

        // 5. 코인 충분한지 확인
        if (user.getPoints() < item.getPrice()) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_FUNDS);
        }

        // 6. 코인 차감
        int newPoints = user.getPoints() - item.getPrice();
        User updatedUser = User.builder().userId(userId).points(newPoints).build();
        userMapper.updateUser(updatedUser);

        // 7. 인벤토리에 아이템 추가
        itemRepository.addUserItem(userId, itemId, false);
    }
}