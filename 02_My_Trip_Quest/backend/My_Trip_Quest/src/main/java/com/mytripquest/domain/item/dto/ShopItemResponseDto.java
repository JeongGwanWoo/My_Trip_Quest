package com.mytripquest.domain.item.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ShopItemResponseDto {
    private final List<ShopItemDto> items;
    private final int currentPage;
    private final int totalPages;
    private final long totalItems;

    @Builder
    public ShopItemResponseDto(List<ShopItemDto> items, int currentPage, int totalPages, long totalItems) {
        this.items = items;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }
}
