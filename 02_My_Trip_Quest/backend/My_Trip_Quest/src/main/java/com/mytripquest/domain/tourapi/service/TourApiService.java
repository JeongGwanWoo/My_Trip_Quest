package com.mytripquest.domain.tourapi.service;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TourApiService {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper;

    // 사용자님이 찾으신 정확한 Base URL (KorService2)
    private final String BASE_URL = "https://apis.data.go.kr/B551011/KorService2";

    // 인코딩된 키 (그대로 사용)
    @Value("${tour.api.key}")
    private String ENCODED_KEY;

    public JsonNode getAreaBasedList(String areaCode, String cat1, int pageNo) {
        try {
            StringBuilder sb = new StringBuilder();

            // 1. 주소 수정: KorService1 -> KorService2
            // 2. 기능 수정: areaBasedList1 -> areaBasedList2
            sb.append(BASE_URL).append("/areaBasedList2");

            sb.append("?serviceKey=").append(ENCODED_KEY);
            sb.append("&numOfRows=10");
            sb.append("&pageNo=").append(pageNo);
            sb.append("&MobileOS=WEB");
            sb.append("&MobileApp=MyTripQuest");
            sb.append("&_type=json");

            if (areaCode != null && !areaCode.isEmpty()) {
                sb.append("&areaCode=").append(areaCode);
            }
            
            if (cat1 != null && !cat1.isEmpty()) {
                sb.append("&cat1=").append(cat1);
            }

            URI uri = URI.create(sb.toString());

            log.info(">>> 최종 요청 URL: {}", uri);

            String response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            return objectMapper.readTree(response);

        } catch (Exception e) {
            log.error("API 요청 중 오류 발생", e);
            throw new RuntimeException("TourAPI Error", e);
        }
    }

    // 시군구(Sigungu) 또는 지역코드 조회 (areaCode1)
    public JsonNode getSigunguList(String areaCode) {
        try {
            StringBuilder sb = new StringBuilder();

            // areaCode1 엔드포인트 사용 (지역코드 조회)
            sb.append(BASE_URL).append("/areaCode2");

            sb.append("?serviceKey=").append(ENCODED_KEY);
            sb.append("&numOfRows=100"); // 시군구 개수는 많을 수 있으므로 넉넉하게
            sb.append("&pageNo=1");
            sb.append("&MobileOS=WEB");
            sb.append("&MobileApp=MyTripQuest");
            sb.append("&_type=json");

            // areaCode가 있으면 해당 지역의 시군구 조회 / 없으면 최상위 지역(서울, 부산 등) 조회
            if (areaCode != null && !areaCode.isEmpty()) {
                sb.append("&areaCode=").append(areaCode);
            }

            URI uri = URI.create(sb.toString());

            log.info(">>> 시군구 조회 URL: {}", uri);

            String response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            return objectMapper.readTree(response);

        } catch (Exception e) {
            log.error("시군구 조회 중 오류 발생", e);
            throw new RuntimeException("TourAPI Error (Sigungu)", e);
        }
    }

    // 대/중/소 분류 조회 (categoryCode2)
    public JsonNode getCategoryList(String cat1, String cat2, String cat3) {
        try {
            StringBuilder sb = new StringBuilder();

            // categoryCode2 엔드포인트 사용
            sb.append(BASE_URL).append("/categoryCode2");

            sb.append("?serviceKey=").append(ENCODED_KEY);
            sb.append("&numOfRows=100");
            sb.append("&pageNo=1");
            sb.append("&MobileOS=WEB");
            sb.append("&MobileApp=MyTripQuest");
            sb.append("&_type=json");

            // 단계별 분류 조회
            if (cat1 != null && !cat1.isEmpty()) {
                sb.append("&cat1=").append(cat1);
            }
            if (cat2 != null && !cat2.isEmpty()) {
                sb.append("&cat2=").append(cat2);
            }
            if (cat3 != null && !cat3.isEmpty()) {
                sb.append("&cat3=").append(cat3);
            }

            URI uri = URI.create(sb.toString());

            log.info(">>> 분류 코드 조회 URL: {}", uri);

            String response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            return objectMapper.readTree(response);

        } catch (Exception e) {
            log.error("분류 코드 조회 중 오류 발생", e);
            throw new RuntimeException("TourAPI Error (Category)", e);
        }
    }

    // 행사 정보 조회 (searchFestival1)
    public JsonNode searchFestival(String eventStartDate, String areaCode, String sigunguCode, int pageNo) {
        try {
            StringBuilder sb = new StringBuilder();

            // 행사 정보 조회 엔드포인트
            sb.append(BASE_URL).append("/searchFestival2");

            sb.append("?serviceKey=").append(ENCODED_KEY);
            sb.append("&numOfRows=10");
            sb.append("&pageNo=").append(pageNo);
            sb.append("&MobileOS=WEB");
            sb.append("&MobileApp=MyTripQuest");
            sb.append("&_type=json");

            // 행사 시작일 (YYYYMMDD) - 없으면 오늘 날짜
            if (eventStartDate == null || eventStartDate.isEmpty()) {
                eventStartDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
            }
            sb.append("&eventStartDate=").append(eventStartDate);

            // 지역 필터
            if (areaCode != null && !areaCode.isEmpty()) {
                sb.append("&areaCode=").append(areaCode);
            }
            if (sigunguCode != null && !sigunguCode.isEmpty()) {
                sb.append("&sigunguCode=").append(sigunguCode);
            }

            URI uri = URI.create(sb.toString());

            log.info(">>> 행사 정보 조회 URL: {}", uri);

            String response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            return objectMapper.readTree(response);

        } catch (Exception e) {
            log.error("행사 정보 조회 중 오류 발생", e);
            throw new RuntimeException("TourAPI Error (Festival)", e);
        }
    }
}