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

    Optional<Item> findItemByName(String name); // Add this method

    List<UserItem> findUserItemsByUserId(Long userId);

    Optional<UserItem> findUserItem(@Param("userId") Long userId, @Param("itemId") Long itemId);

    Optional<UserItem> findEquippedItemBySlot(@Param("userId") Long userId, @Param("slot") Item.ItemSlot slot);

    // == Mutators == //
    void addUserItem(@Param("userId") Long userId, @Param("itemId") Long itemId, @Param("isEquipped") boolean isEquipped);

    void equipItemAndUnequipOthers(@Param("userId") Long userId, @Param("itemId") Long itemId);

    void unequipSlot(@Param("userId") Long userId, @Param("slot") String slot);
}