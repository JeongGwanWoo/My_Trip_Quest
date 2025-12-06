package com.mytripquest.controller;

import com.mytripquest.domain.item.dto.ItemDto;
import com.mytripquest.domain.item.dto.ShopItemDto;
import com.mytripquest.domain.item.entity.UserItem;
import com.mytripquest.domain.item.service.ItemService;
import com.mytripquest.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * @deprecated 상점 목록 조회는 /api/v1/items/shop 을 이용해주세요.
     */
    @GetMapping
    @Deprecated
    public ResponseEntity<List<ItemDto>> getAllItems() {
        List<ItemDto> items = itemService.findAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<UserItem>> getMyInventory() {
        // TODO: 추후 Spring Security 적용 시 로그인한 유저 ID로 변경 필요
        Long userId = 1L;
        List<UserItem> myItems = itemService.findMyItems(userId);
        return ResponseEntity.ok(myItems);
    }

    @GetMapping("/shop")
    public ResponseEntity<ApiResponse<List<ShopItemDto>>> getShopItems() {
        // TODO: 추후 Spring Security 적용 시 로그인한 유저 ID로 변경 필요
        Long userId = 1L;
        List<ShopItemDto> shopItems = itemService.getShopItems(userId);
        return ResponseEntity.ok(ApiResponse.success(shopItems));
    }

    @PostMapping("/{itemId}/buy")
    public ResponseEntity<ApiResponse<Void>> buyItem(@PathVariable Long itemId) {
        // TODO: 추후 Spring Security 적용 시 로그인한 유저 ID로 변경 필요
        Long userId = 1L;
        itemService.buyItem(userId, itemId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}