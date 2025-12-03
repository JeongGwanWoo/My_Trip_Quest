package com.mytripquest.domain.avatar.dto;

import com.mytripquest.domain.item.entity.Item.ItemSlot;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EquippedItemDto {
    private Long itemId;
    private String name;
    private ItemSlot slot;
    private String imageUrl;
}
