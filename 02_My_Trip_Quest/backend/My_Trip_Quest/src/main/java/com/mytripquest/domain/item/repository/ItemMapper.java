package com.mytripquest.domain.item.repository;

import com.mytripquest.domain.item.entity.Item;
import com.mytripquest.domain.item.entity.UserItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional; // ★ 이 import가 필수입니다!

@Mapper
public interface ItemMapper {

	// 1. 아이템 ID로 상세 정보 조회 (이게 없어서 빨간 줄이 떴을 겁니다!)
	Optional<Item> findItemById(Long itemId);

	// 2. 내 아이템 목록 조회
	List<UserItem> findUserItemsByUserId(Long userId);

	// 3. 내 아이템 1개 조회 (보유 여부 확인용)
	Optional<UserItem> findUserItem(@Param("userId") Long userId, @Param("itemId") Long itemId);

	// 4. 특정 슬롯에 장착된 아이템 조회 (Enum 타입 사용)
	Optional<UserItem> findEquippedItemBySlot(@Param("userId") Long userId, @Param("slot") Item.ItemSlot slot); // 혹은
	
	// 기존 메서드들 다 무시하시고, 이거 하나 추가하세요.
	void equipItemAndUnequipOthers(@Param("userId") Long userId, @Param("itemId") Long itemId);
	
	// ★ [추가/부활] 특정 슬롯의 아이템을 전부 해제
	void unequipSlot(@Param("userId") Long userId, @Param("slot") String slot);
}