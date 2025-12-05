package com.mytripquest.domain.item.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long itemId;
    private String name;
    private String slot; // ENUM('HAIR', 'HAT', 'TOP', 'BOTTOM', 'FACE', 'SKIN')
    private String imageUrl;
    private boolean isPurchasable;
    private Integer price;
    private LocalDateTime createdAt;
}
