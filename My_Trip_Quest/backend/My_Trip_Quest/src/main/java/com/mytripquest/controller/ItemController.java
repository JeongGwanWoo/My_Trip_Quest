package com.mytripquest.controller;

import com.mytripquest.domain.item.dto.ItemDto;
import com.mytripquest.domain.item.dto.ShopItemDto;
import com.mytripquest.domain.item.dto.ShopItemResponseDto;
import com.mytripquest.domain.item.entity.UserItem;
import com.mytripquest.domain.item.service.ItemService;
import com.mytripquest.domain.user.service.UserService;
import com.mytripquest.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

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

    // [수정] category 파라미터 추가
    @GetMapping("/shop")
    public ResponseEntity<ApiResponse<ShopItemResponseDto>> getShopItems(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String category) { // category 추가 (필수 아님)

        Long userId = null;
        if (userDetails != null) {
            userId = userService.findIdByEmail(userDetails.getUsername());
        }

        // 서비스로 category 전달
        ShopItemResponseDto shopItemsResponse = itemService.getShopItems(userId, page, size, category);
        
        return ResponseEntity.ok(ApiResponse.success(shopItemsResponse));
    }

    @PostMapping("/{itemId}/buy")
    public ResponseEntity<ApiResponse<Void>> buyItem(@PathVariable Long itemId, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findIdByEmail(userDetails.getUsername());
        itemService.buyItem(userId, itemId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}