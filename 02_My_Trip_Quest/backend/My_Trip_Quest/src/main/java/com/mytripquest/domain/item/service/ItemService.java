package com.mytripquest.domain.item.service;

import com.mytripquest.domain.item.dto.ItemDto;
import com.mytripquest.domain.item.dto.ShopItemDto;
import com.mytripquest.domain.item.entity.Item;
import com.mytripquest.domain.item.entity.UserItem;
import com.mytripquest.domain.item.repository.ItemMapper;
import com.mytripquest.domain.item.repository.ItemRepository;
import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import Slf4j
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Add Slf4j annotation
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<ItemDto> findAllItems() {
        return itemRepository.findAll().stream()
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
                item.getCreatedAt()
        );
    }
    
    public List<UserItem> findMyItems(Long userId) {
        return itemMapper.findUserItemsByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public List<ShopItemDto> getShopItems(Long userId) {
        // 1. Get all items available in the game
        List<Item> allItems = itemRepository.findAll();

        // 2. Get the items the user already owns
        List<UserItem> myItems = findMyItems(userId);
        Set<Long> myItemIds = myItems.stream()
                                     .map(UserItem::getItemId)
                                     .collect(Collectors.toSet());

        // 3. Combine the info to create ShopItemDto list
        return allItems.stream()
                .filter(Item::isPurchasable) // 구매 가능한 아이템만 필터링
                .map(item -> ShopItemDto.builder()
                        .id(item.getItemId())
                        .name(item.getName())
                        .category(item.getSlot() != null ? item.getSlot().name().toLowerCase() : "etc")
                        .price(item.getPrice())
                        .imageUrl(item.getImageUrl())
                        .owned(myItemIds.contains(item.getItemId()))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void buyItem(Long userId, Long itemId) {
        // 1. 아이템 정보 조회
        Item item = itemMapper.findItemById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));

        // 2. 사용자 정보 조회
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 3. 구매 가능 여부 확인
        if (!item.isPurchasable()) {
            throw new BusinessException(ErrorCode.ITEM_NOT_PURCHASABLE);
        }

        // 4. 이미 소유하고 있는지 확인
        itemMapper.findUserItem(userId, itemId).ifPresent(userItem -> {
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
        itemMapper.addUserItem(userId, itemId, false);
    }
}
