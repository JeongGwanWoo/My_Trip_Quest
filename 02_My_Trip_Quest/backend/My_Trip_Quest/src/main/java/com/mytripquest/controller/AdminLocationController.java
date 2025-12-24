package com.mytripquest.controller;

import com.mytripquest.domain.quest.dto.LocationUpdateRequest;
import com.mytripquest.domain.quest.service.QuestService;
import com.mytripquest.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/locations")
@RequiredArgsConstructor
public class AdminLocationController {

    private final QuestService questService;

    // 관광지 위치 및 반경 수정
    @PutMapping("/{locationId}")
    public ApiResponse<Void> updateLocation(@PathVariable Long locationId,
            @RequestBody LocationUpdateRequest request) {
        questService.updateLocation(locationId, request.getLatitude(), request.getLongitude(),
                request.getGpsVerifyRadius());
        return ApiResponse.successWithoutData();
    }
}
