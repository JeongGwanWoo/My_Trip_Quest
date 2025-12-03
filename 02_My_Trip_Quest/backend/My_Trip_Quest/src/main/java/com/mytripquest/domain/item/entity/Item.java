package com.mytripquest.domain.item.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    private Long itemId;
    private String name;
    private String description;
    private ItemType type;
    private ItemSlot slot;
    private String imageUrl;
    private boolean isPurchasable;
    private Integer price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum ItemType {
        EQUIPMENT, CONSUMABLE
    }

    public enum ItemSlot {
        HEAD, EYES, TOP, BOTTOM, SHOES, ACCESSORY, BACKGROUND
    }
}
