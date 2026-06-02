package com.mytripquest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytripquest.domain.item.dto.CreateItemRequest;
import com.mytripquest.domain.item.dto.ItemDto;
import com.mytripquest.domain.item.dto.UpdateItemRequest; // Added import
import com.mytripquest.domain.item.service.AdminItemService;
import com.mytripquest.global.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional; // Added import

@RestController
@RequestMapping("/api/v1/admin/items")
@RequiredArgsConstructor
public class AdminItemController {

    private final AdminItemService adminItemService;
    private final ObjectMapper objectMapper; // To convert itemJson string to DTO

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ItemDto>>> getAllItems(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        Page<ItemDto> items = adminItemService.getAllItems(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(items));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ItemDto>> createItem(@RequestPart("item") String itemJson,
                                                           @RequestPart("image") MultipartFile imageFile) throws IOException {
        CreateItemRequest createItemRequest = objectMapper.readValue(itemJson, CreateItemRequest.class);
        ItemDto createdItem = adminItemService.createItem(createItemRequest, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdItem));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ItemDto>> updateItem(@PathVariable Long itemId,
                                                           @RequestPart("item") String itemJson,
                                                           @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {
        UpdateItemRequest updateItemRequest = objectMapper.readValue(itemJson, UpdateItemRequest.class);
        ItemDto updatedItem = adminItemService.updateItem(itemId, updateItemRequest, imageFile);
        return ResponseEntity.ok(ApiResponse.success(updatedItem));
    }


    @DeleteMapping("/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long itemId) {
        adminItemService.deleteItem(itemId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
