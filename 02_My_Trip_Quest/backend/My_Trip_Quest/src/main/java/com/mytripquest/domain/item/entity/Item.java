package com.mytripquest.domain.item.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    private Long itemId;
    private String name;
    private ItemSlot slot;
    private String imageUrl;
    private boolean purchasable;
    private Integer price;
    private LocalDateTime createdAt;

    public enum ItemSlot {
        HAIR, HAT, TOP, BOTTOM, FACE, SKIN
    }
}
