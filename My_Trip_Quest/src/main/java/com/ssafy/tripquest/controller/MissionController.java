package com.ssafy.tripquest.controller;

import com.ssafy.tripquest.domain.Mission;
import com.ssafy.tripquest.service.MissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@Slf4j
public class MissionController {

    private final MissionService missionService;



    @PostMapping("/photo/{missionId}")
    public ResponseEntity<?> submitPhoto(
            @PathVariable Long missionId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "latitude", required = false) BigDecimal latitude,
            @RequestParam(value = "longitude", required = false) BigDecimal longitude) {

        log.debug("사진 제출 요청: 사용자 ID={}, 미션 ID={}, 위도={}, 경도={}", userId, missionId, latitude, longitude);

        try {
            Map<String, Object> result = missionService.completePhotoMission(userId, missionId, file, latitude, longitude);
            boolean isSuccess = (boolean) result.getOrDefault("success", false);

            if (isSuccess) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (IllegalArgumentException e) {
            // 클라이언트의 잘못된 요청 처리 (예: 필수 데이터 누락)
            log.warn("사진 제출 실패 (잘못된 요청): {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            // 예상치 못한 서버 측 오류 처리
            log.error("사진 제출 처리 중 예외 발생", e);
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "서버 내부 오류가 발생했습니다."));
        }
    }
}
