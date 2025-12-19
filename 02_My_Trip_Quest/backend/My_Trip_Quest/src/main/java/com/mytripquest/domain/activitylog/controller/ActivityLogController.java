package com.mytripquest.domain.activitylog.controller;

import com.mytripquest.domain.activitylog.dto.ActivityLogResponseDto;
import com.mytripquest.domain.activitylog.service.ActivityLogService;
import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;
    private final UserMapper userMapper;

    @Operation(summary = "내 활동 로그 조회", description = "현재 로그인한 사용자의 활동 로그 목록을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<List<ActivityLogResponseDto>> getMyActivityLogs(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            // PreAuthorize("isAuthenticated()")가 이 부분을 처리하지만, 방어적인 코드로 남겨둡니다.
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        User user = userMapper.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<ActivityLogResponseDto> logs = activityLogService.getLogsByUserId(user.getUserId());
        return ResponseEntity.ok(logs);
    }
}
