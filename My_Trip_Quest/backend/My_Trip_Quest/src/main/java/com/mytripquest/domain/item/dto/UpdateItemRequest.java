package com.mytripquest.domain.item.dto;

import com.mytripquest.domain.item.entity.Item.ItemSlot;
import org.springframework.web.multipart.MultipartFile; // This is not directly used in a record for JSON parsing, but for clarity.

public record UpdateItemRequest(
    String name,
    ItemSlot slot,
    Integer price,
    boolean purchasable,
    boolean imageChanged // Indicates if a new image file is provided
) {}
