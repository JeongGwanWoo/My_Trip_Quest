package com.mytripquest.controller;

import com.mytripquest.domain.item.dto.ItemDto;
import com.mytripquest.domain.item.dto.ShopItemDto;
import com.mytripquest.domain.item.entity.UserItem;
import com.mytripquest.domain.item.service.ItemService;
import com.mytripquest.domain.user.service.UserService; // Import UserService
import com.mytripquest.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Import AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails; // Import UserDetails
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final UserService userService; // Inject UserService

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
    public ResponseEntity<List<UserItem>> getMyInventory(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findIdByEmail(userDetails.getUsername());
        List<UserItem> myItems = itemService.findMyItems(userId);
        return ResponseEntity.ok(myItems);
    }

    @GetMapping("/shop")
    public ResponseEntity<ApiResponse<List<ShopItemDto>>> getShopItems(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findIdByEmail(userDetails.getUsername());
        List<ShopItemDto> shopItems = itemService.getShopItems(userId);
        return ResponseEntity.ok(ApiResponse.success(shopItems));
    }

    @PostMapping("/{itemId}/buy")
    public ResponseEntity<ApiResponse<Void>> buyItem(@PathVariable Long itemId, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findIdByEmail(userDetails.getUsername());
        itemService.buyItem(userId, itemId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}