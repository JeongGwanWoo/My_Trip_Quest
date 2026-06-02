package com.mytripquest.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mytripquest.domain.ai.dto.LocationRadiusRequest;
import com.mytripquest.domain.ai.dto.RadiusEstimateResult;
import com.mytripquest.domain.quest.service.QuestService;
import com.mytripquest.domain.tourapi.service.TourApiService;
import com.mytripquest.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/quests")
@RequiredArgsConstructor
public class AdminQuestController {

    private final TourApiService tourApiService;
    private final QuestService questService;

    // 1. TourAPI 관광지 목록 조회 (프록시)
    @GetMapping("/tourapi/attractions")
    public ApiResponse<JsonNode> getTourApiAttractions(
            @RequestParam String areaCode,
            @RequestParam(required = false, defaultValue = "12") String contentTypeId, // 12: 관광지
            @RequestParam(required = false, defaultValue = "10") int numOfRows,
            @RequestParam(required = false, defaultValue = "1") int pageNo) {

        JsonNode result = tourApiService.getAreaBasedList(areaCode, contentTypeId, pageNo, numOfRows);
        return ApiResponse.success(result);
    }

    // 2. 퀘스트 생성 요청
    @PostMapping("/generate")
    public ApiResponse<Integer> generateQuests(@RequestBody Map<String, Object> requestBody) {
        // requestBody: { "items": [...], "types": ["ARRIVAL", "PHOTO"], "areaCode": "1"
        // }
        List<Map<String, Object>> items = (List<Map<String, Object>>) requestBody.get("items");
        List<String> types = (List<String>) requestBody.get("types");
        String areaCode = (String) requestBody.get("areaCode");

        int count = questService.generateQuestsFromTourApi(items, types, areaCode);
        return ApiResponse.success(count + "개의 퀘스트가 생성되었습니다.", count); // data에 count(int) 포함
    }

    // 3. AI 반경 추정 (단일)
    @PostMapping("/ai/estimate-radius")
    public ApiResponse<Integer> estimateRadius(@RequestBody Map<String, String> request) {
        String locationName = request.get("locationName");
        String address = request.get("address");
        int radius = questService.estimateLocationRadius(locationName, address);
        return ApiResponse.success(radius);
    }

    // 3-1. AI 반경 추정 (배치 - 토큰 절약)
    @PostMapping("/ai/estimate-radius-batch")
    public ApiResponse<List<RadiusEstimateResult>> estimateRadiusBatch(
            @RequestBody List<LocationRadiusRequest> locations) {
        List<RadiusEstimateResult> results = questService.estimateLocationRadiusBatch(locations);
        return ApiResponse.success(results);
    }

    // 3-2. 관광지 AI 반경 일괄 재산정 (여러 관광지 선택 → 배치 처리 → DB 업데이트)
    @PostMapping("/locations/batch-recalculate-radius")
    public ApiResponse<Map<String, Object>> batchRecalculateRadius(@RequestBody List<Long> locationIds) {
        Map<String, Object> result = questService.batchRecalculateRadius(locationIds);
        return ApiResponse.success(result);
    }

    // 4. 관광지 반경 수정
    @PutMapping("/locations/{locationId}")
    public ApiResponse<Void> updateLocationRadius(@PathVariable Long locationId,
            @RequestBody Map<String, Integer> request) {
        int radius = request.get("radius");
        questService.updateLocationRadius(locationId, radius);
        return ApiResponse.successWithoutData();
    }

    // 5. 퀘스트 수동 추가
    @PostMapping("/locations/{locationId}/quests")
    public ApiResponse<Void> addQuest(@PathVariable Long locationId,
            @RequestBody com.mytripquest.domain.quest.entity.Quest quest) {
        quest.setLocationId(locationId);
        // ID generation logic needed? For now, we assume frontend sends ID or we handle
        // it?
        // User requested "Add", implies new.
        // I will assume the frontend generator handles ID logic or I need a simple one.
        // For now, I'll let the user input ID in the frontend modal (to be safe).
        questService.createQuest(quest);
        return ApiResponse.successWithoutData();
    }

    // 6. 퀘스트 삭제
    @DeleteMapping("/{questId}")
    public ApiResponse<Void> deleteQuest(@PathVariable Long questId) {
        questService.deleteQuest(questId);
        return ApiResponse.successWithoutData();
    }
}
