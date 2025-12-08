package com.mytripquest.controller;

import com.mytripquest.domain.user.dto.UserRequestDto;
import com.mytripquest.domain.user.service.UserService;
import com.mytripquest.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
