package com.mytripquest.domain.item.dto;

import com.mytripquest.domain.item.entity.Item.ItemSlot;

public record CreateItemRequest(
    String name,
    ItemSlot slot,
    Integer price,
    boolean purchasable
) {}
