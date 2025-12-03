package com.mytripquest.domain.avatar.service;

import com.mytripquest.domain.avatar.dto.AvatarResponse;
import com.mytripquest.domain.avatar.dto.EquipRequest;
import com.mytripquest.domain.avatar.dto.EquippedItemDto;
import com.mytripquest.domain.item.entity.Item;
import com.mytripquest.domain.item.entity.UserItem;
import com.mytripquest.domain.item.repository.ItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

    private final ItemMapper itemMapper;

    @Override
    @Transactional(readOnly = true)
    public AvatarResponse getAvatar(Long userId) {
        // 1. 유저가 소유한 아이템 목록 조회
        List<UserItem> userItems = itemMapper.findUserItemsByUserId(userId);

        // 2. 착용중인(isEquipped = true) 아이템만 필터링
        List<EquippedItemDto> equippedItems = userItems.stream()
                .filter(UserItem::isEquipped)
                .map(userItem -> {
                    // 3. 아이템 상세 정보 조회
                    Item item = itemMapper.findItemById(userItem.getItemId())
                            .orElseThrow(() -> new RuntimeException("Item not found: " + userItem.getItemId()));
                    // 4. DTO로 변환
                    return EquippedItemDto.builder()
                            .itemId(item.getItemId())
                            .name(item.getName())
                            .slot(item.getSlot())
                            .imageUrl(item.getImageUrl())
                            .build();
                })
                .collect(Collectors.toList());

        return AvatarResponse.builder().equippedItems(equippedItems).build();
    }

    @Override
    @Transactional
    public void equipItem(Long userId, EquipRequest equipRequest) {
        Long itemIdToEquip = equipRequest.getItemId();

        // 1. 착용할 아이템이 유저의 소유인지 확인
        UserItem userItemToEquip = itemMapper.findUserItem(userId, itemIdToEquip)
                .orElseThrow(() -> new RuntimeException("User does not own this item"));

        // 2. 착용할 아이템의 상세 정보(특히 slot) 조회
        Item itemToEquip = itemMapper.findItemById(itemIdToEquip)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // 3. 동일한 슬롯에 이미 착용중인 아이템이 있는지 확인
        itemMapper.findEquippedItemBySlot(userId, itemToEquip.getSlot().name())
                .ifPresent(currentlyEquippedItem -> {
                    // 4. 있다면, 해당 아이템을 착용 해제 상태로 변경
                    if (!currentlyEquippedItem.getItemId().equals(itemIdToEquip)) {
                        currentlyEquippedItem.setEquipped(false); // setEquipped는 UserItem에 추가 필요
                        itemMapper.updateUserItem(currentlyEquippedItem);
                    }
                });

        // 5. 새로 요청된 아이템을 착용 상태로 변경
        userItemToEquip.setEquipped(true); // setEquipped는 UserItem에 추가 필요
        itemMapper.updateUserItem(userItemToEquip);
    }
}
