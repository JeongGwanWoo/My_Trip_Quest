package com.mytripquest.domain.item.repository;

import com.mytripquest.domain.item.entity.Item;
import com.mytripquest.domain.item.entity.UserItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ItemMapper {
    void insertItem(Item item);

    void updateItem(Item item);

    Optional<Item> findById(Long itemId);

    // 호환성 유지용
    Optional<Item> findItemById(Long itemId);

    void deleteItem(Long itemId);

    List<Item> findAllItems();

    List<Item> findAll(@Param("pageable") Pageable pageable);

    long count();

    Optional<Item> findItemByName(String name);

    // Shop용
    List<Item> findShopItemsWithPagination(@Param("limit") int limit, @Param("offset") long offset,
            @Param("category") String category);

    long countShopItems(@Param("category") String category);

    // UserItem 관련
    List<UserItem> findUserItemsByUserId(Long userId);

    Optional<UserItem> findUserItem(@Param("userId") Long userId, @Param("itemId") Long itemId);

    void addUserItem(@Param("userId") Long userId, @Param("itemId") Long itemId,
            @Param("isEquipped") boolean isEquipped);

    Optional<UserItem> findEquippedItemBySlot(@Param("userId") Long userId, @Param("slot") Item.ItemSlot slot);

    void equipItemAndUnequipOthers(@Param("userId") Long userId, @Param("itemId") Long itemId);

    void unequipSlot(@Param("userId") Long userId, @Param("slot") Item.ItemSlot slot);
}
