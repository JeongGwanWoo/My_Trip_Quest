package com.mytripquest.domain.avatar.service;

import com.mytripquest.domain.avatar.dto.AvatarResponse;
import com.mytripquest.domain.avatar.dto.EquipRequest;
import com.mytripquest.domain.avatar.dto.EquippedItemDto;
import com.mytripquest.domain.item.entity.Item;
import com.mytripquest.domain.item.entity.UserItem;
import com.mytripquest.domain.item.repository.ItemMapper;
import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public AvatarResponse getAvatar(Long userId) {
        // 0. 유저 정보 조회 (포인트 가져오기 위함)
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 1. 유저가 소유한 아이템 목록 조회
        List<UserItem> userItems = itemMapper.findUserItemsByUserId(userId);

        // 2. 착용중인(isEquipped = true) 아이템만 필터링하여 DTO로 변환
        List<EquippedItemDto> equippedItems = userItems.stream()
                .filter(UserItem::isEquipped)
                .filter(ui -> ui.getItem() != null)
                .map(userItem -> {
                    Item item = userItem.getItem();
                    return EquippedItemDto.builder()
                            .itemId(item.getItemId())
                            .name(item.getName())
                            .slot(item.getSlot().name())
                            .imageUrl(item.getImageUrl())
                            .build();
                })
                .collect(Collectors.toList());

        // 3. 최종 응답 DTO에 포인트와 아이템 목록을 담아 반환
        return AvatarResponse.builder()
                .points(user.getPoints())
                .equippedItems(equippedItems)
                .build();
    }

    @Override
    public void equipItem(Long userId, EquipRequest equipRequest) {
        Long itemIdToEquip = equipRequest.getItemId();

        itemMapper.findUserItem(userId, itemIdToEquip)
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));

        itemMapper.equipItemAndUnequipOthers(userId, itemIdToEquip);
    }
    
    @Override
    @Transactional
    public void unequipSlot(Long userId, String slot) {
        itemMapper.unequipSlot(userId, slot);
    }
}
