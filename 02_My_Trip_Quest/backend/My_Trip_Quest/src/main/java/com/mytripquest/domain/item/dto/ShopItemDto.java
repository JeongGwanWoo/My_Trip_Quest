package com.mytripquest.domain.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShopItemDto {
    private long id;
    private String name;
    private String category;
    private int price;
    private String imageUrl;
    private boolean owned;
}
