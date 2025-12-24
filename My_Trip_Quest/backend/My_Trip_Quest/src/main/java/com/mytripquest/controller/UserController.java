package com.mytripquest.controller;

import com.mytripquest.domain.pointhistory.dto.PointHistoryResponse;
import com.mytripquest.domain.pointhistory.service.PointHistoryService;
import com.mytripquest.domain.quest.dto.CompletedMissionResponse;
import com.mytripquest.domain.quest.service.QuestService;
import com.mytripquest.domain.user.dto.UserRequestDto;
import com.mytripquest.domain.user.dto.UserProfileResponseDto;
import com.mytripquest.domain.user.repository.UserMapper;
import com.mytripquest.domain.user.service.ProfileService;
import com.mytripquest.domain.user.service.UserService;
import com.mytripquest.global.ApiResponse;
import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ProfileService profileService;
    private final PointHistoryService pointHistoryService;
    private final QuestService questService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody UserRequestDto.Register request) {
        userService.register(request);
        return ResponseEntity.ok(ApiResponse.success("회원가입이 성공적으로 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody UserRequestDto.Login request) {
        String token = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인이 성공적으로 완료되었습니다.", Collections.singletonMap("token", token)));
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(
            @RequestBody UserRequestDto.SendVerificationCode request) {
        log.info("Received request to send verification code to: {}", request.getEmail());
        userService.sendVerificationCode(request.getEmail()); // No return value
        return ResponseEntity.ok(ApiResponse.success("인증 코드가 이메일로 발송되었습니다. 이메일을 확인해주세요."));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<Void>> verifyCode(@RequestBody UserRequestDto.VerifyCode request) {
        boolean isVerified = userService.verifyCode(request.getEmail(), request.getCode());

        if (isVerified) {
            return ResponseEntity.ok(ApiResponse.success("이메일 인증이 완료되었습니다."));
        } else {
            // 400 Bad Request 리턴
            return ResponseEntity.badRequest().body(ApiResponse.failure("인증 코드가 일치하지 않습니다."));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody UserRequestDto.ResetPassword request) {
        userService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다. 새 비밀번호로 로그인해주세요."));
    }

    @PostMapping("/social-signup")
    public ResponseEntity<ApiResponse<Object>> socialSignup(@Valid @RequestBody UserRequestDto.SocialSignup request) {
        String token = userService.socialSignup(request);
        return ResponseEntity
                .ok(ApiResponse.success("소셜 회원가입이 성공적으로 완료되었습니다.", Collections.singletonMap("token", token)));
    }

    @PostMapping("/send-reset-code")
    public ResponseEntity<ApiResponse<Void>> sendResetCode(@RequestBody UserRequestDto.SendVerificationCode request) {
        userService.sendPasswordResetCode(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("인증 코드가 발송되었습니다."));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponseDto>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure("인증되지 않은 사용자입니다."));
        }
        Long userId = userMapper.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getUserId();
        UserProfileResponseDto profile = profileService.getProfileData(userId);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping("/me/point-history")
    public ResponseEntity<ApiResponse<List<PointHistoryResponse>>> getPointHistory(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure("인증되지 않은 사용자입니다."));
        }
        Long userId = userMapper.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getUserId();
        List<PointHistoryResponse> history = pointHistoryService.getPointHistory(userId);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/me/completed-missions")
    public ResponseEntity<ApiResponse<List<CompletedMissionResponse>>> getCompletedMissions(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure("인증되지 않은 사용자입니다."));
        }
        Long userId = userMapper.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND))
                .getUserId();
        List<CompletedMissionResponse> missions = questService.getCompletedMissions(userId);
        return ResponseEntity.ok(ApiResponse.success(missions));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<Object>> checkNickname(@RequestParam String nickname) {
        boolean isAvailable = userService.checkNicknameAvailability(nickname);
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("isAvailable", isAvailable)));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<String>> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserRequestDto.Update request) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure("인증되지 않은 사용자입니다."));
        }
        userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("회원정보가 성공적으로 수정되었습니다."));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<String>> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.failure("인증되지 않은 사용자입니다."));
        }
        userService.deleteUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 성공적으로 처리되었습니다."));
    }
}
