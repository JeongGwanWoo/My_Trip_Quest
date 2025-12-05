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
        // 1. 유저가 소유한 아이템 목록 조회 (MyBatis ResultMap 덕분에 Item 정보도 이미 안에 들어있음!)
        List<UserItem> userItems = itemMapper.findUserItemsByUserId(userId);

     // 2. 착용중인(isEquipped = true) 아이템만 필터링
        List<EquippedItemDto> equippedItems = userItems.stream()
                .filter(UserItem::isEquipped) // 장착된 것만
                .filter(ui -> ui.getItem() != null) // 혹시 모를 Null 체크
                .map(userItem -> {
                    
                    // ★ 수정 포인트: findItemById 호출 금지! (DB 또 가면 안 됨)
                    // MyBatis가 이미 가져온 Item 객체를 바로 꺼냅니다.
                    Item item = userItem.getItem(); 

                    return EquippedItemDto.builder()
                            // ★ 만약 여기서 getItemId()에 빨간줄이 뜬다면 getId()로 바꿔보세요!
                            // (Item 엔티티 필드명이 id인지 itemId인지에 따라 다름)
                            .itemId(item.getItemId()) 
                            
                            .name(item.getName())
                            .slot(item.getSlot().name()) // Enum을 문자열(String)로 변환
                            .imageUrl(item.getImageUrl())
                            .build();
                })
                .collect(Collectors.toList());

        return AvatarResponse.builder().equippedItems(equippedItems).build();
    }

    @Override
    // @Transactional <-- 필요 없습니다. 한 방 쿼리라 안전합니다.
    public void equipItem(Long userId, EquipRequest equipRequest) {
        Long itemIdToEquip = equipRequest.getItemId();

        // 1. 아이템 보유 확인 (최소한의 안전장치)
        itemMapper.findUserItem(userId, itemIdToEquip)
                .orElseThrow(() -> new RuntimeException("보유하지 않은 아이템입니다."));

        // 2. [한 방 해결] 같은 부위 다 벗기고 얘만 입히기!
        itemMapper.equipItemAndUnequipOthers(userId, itemIdToEquip);
        
    }
    
    @Override
    @Transactional
    public void unequipSlot(Long userId, String slot) {
        // 해당 슬롯 싹 비우기
        itemMapper.unequipSlot(userId, slot);
    }
}
