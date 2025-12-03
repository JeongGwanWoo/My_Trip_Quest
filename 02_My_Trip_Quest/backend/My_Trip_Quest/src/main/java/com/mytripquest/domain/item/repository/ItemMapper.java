package com.mytripquest.domain.item.repository;

import com.mytripquest.domain.item.entity.Item;
import com.mytripquest.domain.item.entity.UserItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ItemMapper {

    Optional<Item> findItemById(Long itemId);

    List<UserItem> findUserItemsByUserId(Long userId);

    Optional<UserItem> findUserItem(@Param("userId") Long userId, @Param("itemId") Long itemId);

    void updateUserItem(UserItem userItem);

    // Find equipped item for a specific slot
    Optional<UserItem> findEquippedItemBySlot(@Param("userId") Long userId, @Param("slot") String slot);
    
}
