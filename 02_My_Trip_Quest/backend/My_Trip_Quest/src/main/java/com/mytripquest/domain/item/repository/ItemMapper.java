package com.mytripquest.domain.item.repository;

import com.mytripquest.domain.item.entity.Item;
import com.mytripquest.domain.item.entity.UserItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ItemMapper {

    // == Finders == //
    Optional<Item> findItemById(Long itemId);

    Optional<Item> findItemByName(String name);

    List<UserItem> findUserItemsByUserId(Long userId);

    Optional<UserItem> findUserItem(@Param("userId") Long userId, @Param("itemId") Long itemId);

    Optional<UserItem> findEquippedItemBySlot(@Param("userId") Long userId, @Param("slot") Item.ItemSlot slot);

    // [수정됨] 상점 목록 조회: 카테고리 파라미터 추가 & 메서드명 변경 (XML ID와 일치)
    List<Item> findShopItemsWithPagination(
            @Param("offset") int offset, 
            @Param("limit") int limit, 
            @Param("category") String category
    );

    // [수정됨] 상점 아이템 개수: 카테고리 파라미터 추가 & 메서드명 변경 (XML ID와 일치)
    long countShopItems(@Param("category") String category);

    // == Mutators == //
    void addUserItem(@Param("userId") Long userId, @Param("itemId") Long itemId, @Param("isEquipped") boolean isEquipped);

    void equipItemAndUnequipOthers(@Param("userId") Long userId, @Param("itemId") Long itemId);

    void unequipSlot(@Param("userId") Long userId, @Param("slot") String slot);
}