package com.mytripquest.controller;

import com.mytripquest.domain.item.dto.ItemDto;
import com.mytripquest.domain.item.entity.UserItem; // ★ import 확인
import com.mytripquest.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // 1. (기존) 상점용 전체 아이템 조회
    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItems() {
        List<ItemDto> items = itemService.findAllItems();
        return ResponseEntity.ok(items);
    }

    // 2. (추가) 내 인벤토리 조회 ★ 여기를 추가하세요!
    // 호출 주소: /api/items/inventory
    @GetMapping("/inventory")
    public ResponseEntity<List<UserItem>> getMyInventory() {
        
        // ★ 테스트용: 로그인 기능 없으므로 1번 유저로 강제 고정
    	// TODO: 추후 Spring Security 적용 시 로그인한 유저 ID로 변경 필요
        Long userId = 1L; 
        
        // Service를 통해 MyBatis Mapper 호출
        List<UserItem> myItems = itemService.findMyItems(userId);
        
        return ResponseEntity.ok(myItems);
    }
}