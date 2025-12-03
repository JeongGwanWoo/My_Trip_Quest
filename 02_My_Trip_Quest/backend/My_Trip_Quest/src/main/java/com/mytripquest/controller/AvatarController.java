package com.mytripquest.controller;

import com.mytripquest.domain.avatar.dto.AvatarResponse;
import com.mytripquest.domain.avatar.dto.EquipRequest;
import com.mytripquest.domain.avatar.service.AvatarService;
import com.mytripquest.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/avatar")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

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
}
