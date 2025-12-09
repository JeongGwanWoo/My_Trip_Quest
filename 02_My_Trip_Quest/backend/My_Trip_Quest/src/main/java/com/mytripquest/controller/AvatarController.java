package com.mytripquest.controller;

import com.mytripquest.domain.avatar.dto.AvatarResponse;
import com.mytripquest.domain.avatar.dto.EquipRequest;
import com.mytripquest.domain.avatar.dto.UnequipRequest;
import com.mytripquest.domain.avatar.service.AvatarService;
import com.mytripquest.domain.user.service.UserService; // Import UserService
import com.mytripquest.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Import AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails; // Import UserDetails
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/avatar")
@RequiredArgsConstructor
public class AvatarController {

	private final AvatarService avatarService;
    private final UserService userService; // Inject UserService

    @PostMapping("/equip")
    public ResponseEntity<ApiResponse<?>> equipItem(@RequestBody EquipRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findIdByEmail(userDetails.getUsername());
        avatarService.equipItem(userId, request);
        return ResponseEntity.ok(ApiResponse.successWithoutData());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AvatarResponse>> getMyAvatar(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findIdByEmail(userDetails.getUsername());
        AvatarResponse avatarResponse = avatarService.getAvatar(userId);
        return ResponseEntity.ok(ApiResponse.success(avatarResponse));
    }
    
    @PostMapping("/unequip")
    public ResponseEntity<ApiResponse<?>> unequipItem(@RequestBody UnequipRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findIdByEmail(userDetails.getUsername());
        avatarService.unequipSlot(userId, request.getSlot());
        return ResponseEntity.ok(ApiResponse.successWithoutData());
    }
}
