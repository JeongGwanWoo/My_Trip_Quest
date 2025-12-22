package com.mytripquest.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mytripquest.domain.tourapi.service.TourApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tour") // 기본 경로 설정
public class TourApiController {

    private final TourApiService tourApiService;

    /**
     * 지역 기반 관광지 조회
     * 요청 URL 예시: /api/v1/tour/area?areaCode=6&pageNo=1
     */
    @GetMapping("/area")
    public ResponseEntity<JsonNode> getAreaBasedList(
            @RequestParam(name = "areaCode", required = false) String areaCode, // 지역 코드는 없을 수도 있음 (전국 조회)
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo // 페이지 번호 없으면 1페이지 기본
    ) {
        log.info("Controller 요청 진입 - areaCode: {}, pageNo: {}", areaCode, pageNo);

        JsonNode result = tourApiService.getAreaBasedList(areaCode, pageNo);

        return ResponseEntity.ok(result);
    }

    /**
     * 시군구 조회 (areaCode가 없으면 광역시/도 조회, 있으면 시군구 조회)
     * 요청 URL 예시: /api/v1/tour/sigungu?areaCode=6
     */
    @GetMapping("/sigungu")
    public ResponseEntity<JsonNode> getSigunguCode(
            @RequestParam(name = "areaCode", required = false) String areaCode) {
        log.info("Controller 시군구 조회 요청 - areaCode: {}", areaCode);

        JsonNode result = tourApiService.getSigunguList(areaCode);

        return ResponseEntity.ok(result);
    }

    /**
     * 대/중/소 분류 조회 (categoryCode2)
     * 요청 URL 예시: /api/v1/tour/category?cat1=1&cat2=1&cat3=1
     */
    @GetMapping("/category")
    public ResponseEntity<JsonNode> getCategoryList(
            @RequestParam(name = "cat1", required = false) String cat1, // 대분류
            @RequestParam(name = "cat2", required = false) String cat2, // 중분류
            @RequestParam(name = "cat3", required = false) String cat3) {
        log.info("Controller 대/중/소 분류 조회 요청 - cat1: {}, cat2: {}, cat3: {}", cat1, cat2, cat3);

        JsonNode result = tourApiService.getCategoryList(cat1, cat2, cat3);

        return ResponseEntity.ok(result);
    }

    /**
     * 행사 정보 조회 (searchFestival1)
     * 요청 URL 예시: /api/v1/tour/festival?eventStartDate=20230101&areaCode=1
     */
    @GetMapping("/festival")
    public ResponseEntity<JsonNode> getFestivalList(
            @RequestParam(name = "eventStartDate") String eventStartDate, // 행사 시작일 (YYYYMMDD) 필수
            @RequestParam(name = "areaCode", required = false) String areaCode,
            @RequestParam(name = "sigunguCode", required = false) String sigunguCode,
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo) {
        log.info("Controller 행사 조회 요청 - date: {}, area: {}", eventStartDate, areaCode);

        JsonNode result = tourApiService.searchFestival(eventStartDate, areaCode, sigunguCode, pageNo);

        return ResponseEntity.ok(result);
    }
}