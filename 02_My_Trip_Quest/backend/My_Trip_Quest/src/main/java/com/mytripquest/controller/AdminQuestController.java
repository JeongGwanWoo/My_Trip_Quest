package com.mytripquest.controller;

import com.fasterxml.jackson.databind.JsonNode;
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
    public ApiResponse<String> generateQuests(@RequestBody Map<String, Object> requestBody) {
        // requestBody: { "items": [...], "types": ["ARRIVAL", "PHOTO"], "areaCode": "1"
        // }
        List<Map<String, Object>> items = (List<Map<String, Object>>) requestBody.get("items");
        List<String> types = (List<String>) requestBody.get("types");
        String areaCode = (String) requestBody.get("areaCode");

        int count = questService.generateQuestsFromTourApi(items, types, areaCode);
        return ApiResponse.success(count + "개의 퀘스트가 생성되었습니다.");
    }
}
