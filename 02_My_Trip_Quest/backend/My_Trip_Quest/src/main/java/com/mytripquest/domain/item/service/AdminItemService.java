package com.mytripquest.domain.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.mytripquest.domain.item.dto.CreateItemRequest;
import com.mytripquest.domain.item.dto.ItemDto;
import com.mytripquest.domain.item.dto.UpdateItemRequest;

public interface AdminItemService {
    Page<ItemDto> getAllItems(Pageable pageable);
    ItemDto createItem(CreateItemRequest request, MultipartFile imageFile);
    ItemDto updateItem(Long itemId, UpdateItemRequest request, MultipartFile imageFile);
    void deleteItem(Long itemId);
}
