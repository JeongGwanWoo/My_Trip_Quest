package com.mytripquest.controller;

import com.mytripquest.domain.user.dto.UserRequestDto;
import com.mytripquest.domain.user.dto.UserResponseDto;
import com.mytripquest.domain.user.service.UserService;
import com.mytripquest.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody UserRequestDto.Register request) {
        userService.register(request);
        return ResponseEntity.ok(new ApiResponse(true, "회원가입이 성공적으로 완료되었습니다.", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody UserRequestDto.Login request) {
        String token = userService.login(request);
        return ResponseEntity.ok(new ApiResponse(true, "로그인이 성공적으로 완료되었습니다.", Collections.singletonMap("token", token)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponseDto.ProfileResponseDto>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure("인증되지 않은 사용자입니다."));
        }
        UserResponseDto.ProfileResponseDto profile = userService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserRequestDto.Update request) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure("인증되지 않은 사용자입니다."));
        }
        userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("회원정보가 성공적으로 수정되었습니다."));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure("인증되지 않은 사용자입니다."));
        }
        userService.deleteUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 성공적으로 처리되었습니다."));
    }
}
