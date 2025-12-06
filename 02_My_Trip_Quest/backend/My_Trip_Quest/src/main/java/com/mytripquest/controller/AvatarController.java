package com.mytripquest.controller;

import com.mytripquest.domain.avatar.dto.AvatarResponse;
import com.mytripquest.domain.avatar.dto.EquipRequest;
import com.mytripquest.domain.avatar.dto.UnequipRequest;
import com.mytripquest.domain.avatar.service.AvatarService;
import com.mytripquest.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/avatar")
@RequiredArgsConstructor
public class AvatarController {

	private final AvatarService avatarService;

    // 장착 요청 처리 (자동 저장)
    @PostMapping("/equip")
    public ResponseEntity<String> equipItem(@RequestBody EquipRequest request) {
        
        // ★ 테스트용: 1번 유저 고정
    	// TODO: 추후 Spring Security 적용 시 로그인한 유저 ID로 변경 필요
        Long userId = 1L;
        
        avatarService.equipItem(userId, request);
        
        return ResponseEntity.ok("장착 완료");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<AvatarResponse>> getAvatar(@PathVariable Long userId) {
        AvatarResponse avatarResponse = avatarService.getAvatar(userId);
        return ResponseEntity.ok(ApiResponse.success(avatarResponse));
    }

    @PostMapping("/{userId}/equip")
    public ResponseEntity<ApiResponse<?>> equipItem(@PathVariable Long userId, @RequestBody EquipRequest equipRequest) {
        avatarService.equipItem(userId, equipRequest);
        return ResponseEntity.ok(ApiResponse.successWithoutData());
    }
    
    @PostMapping("/unequip")
    public ResponseEntity<String> unequipItem(@RequestBody UnequipRequest request) {
        Long userId = 1L; // 테스트용
        avatarService.unequipSlot(userId, request.getSlot());
        return ResponseEntity.ok("해제 완료");
    }
}
