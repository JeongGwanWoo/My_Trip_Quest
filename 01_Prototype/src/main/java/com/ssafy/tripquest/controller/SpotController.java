package com.ssafy.tripquest.controller;

import com.ssafy.tripquest.domain.ArrivalRequest;
import com.ssafy.tripquest.domain.Mission;
import com.ssafy.tripquest.domain.TouristSpot;
import com.ssafy.tripquest.domain.VerificationResponse;
import com.ssafy.tripquest.mapper.TouristSpotMapper;
import com.ssafy.tripquest.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spots")
@RequiredArgsConstructor
public class SpotController {

    private final MissionService missionService;
    private final TouristSpotMapper touristSpotMapper;

    @GetMapping
    public ResponseEntity<List<TouristSpot>> getAllSpots() {
        return ResponseEntity.ok(touristSpotMapper.findAll());
    }

    @PostMapping("/{spotNo}/verify-arrival")
    public ResponseEntity<VerificationResponse> verifyArrival(
            @PathVariable Integer spotNo,
            @RequestBody ArrivalRequest request) {
        
        // TODO: 임시로 사용자 ID를 1L로 하드코딩. 향후 인증 기능 구현 시 수정 필요.
        Long userId = 1L;

        VerificationResponse response = missionService.verifyArrival(
                userId,
                spotNo,
                request.getLatitude(),
                request.getLongitude()
        );

        if (response.isVerified()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{spotNo}/missions")
    public ResponseEntity<List<Mission>> getMissionsForSpot(@PathVariable Integer spotNo) {
        List<Mission> missions = missionService.getMissionsBySpotNo(spotNo);
        return ResponseEntity.ok(missions);
    }
}
