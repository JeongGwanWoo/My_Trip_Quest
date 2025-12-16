package com.mytripquest.controller;

import com.mytripquest.domain.user.dto.UserProfileResponseDto;
import com.mytripquest.domain.user.service.ProfileService;
import com.mytripquest.domain.user.repository.UserMapper;
import com.mytripquest.global.ApiResponse;
import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final UserMapper userMapper;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userMapper.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getUserId();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileResponseDto>> getProfile() {
        Long userId = getCurrentUserId();
        UserProfileResponseDto profileData = profileService.getProfileData(userId);
        return ResponseEntity.ok(ApiResponse.success(profileData));
    }
}
