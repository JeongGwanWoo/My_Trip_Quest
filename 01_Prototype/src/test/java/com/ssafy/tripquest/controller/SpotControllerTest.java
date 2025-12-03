package com.ssafy.tripquest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.tripquest.domain.ArrivalRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql; // SQL 파일 실행을 위한 임포트
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // 스프링 부트 애플리케이션 전체 컨텍스트 로드
@AutoConfigureMockMvc // MockMvc 자동 구성
// 테스트 전에 data.sql 파일을 실행하여 DB를 초기화합니다.
// 이 어노테이션을 사용하려면 resources/data.sql에 필요한 데이터가 있어야 합니다.
// 예를 들어, INSERT INTO tourist_spot (spot_no, spot_name, latitude, longitude) VALUES (1, '테스트 관광지', 37.5512, 126.9882);
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class SpotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // JSON 직렬화를 위해 필요

    @Test
    @DisplayName("관광지 도착 검증 성공 테스트 - 실제 위치 근처")
    void verifyArrival_Success_RealLocationNearSpot() throws Exception {
        // given
        Integer spotNo = 1; // data.sql에 있는 '경복궁'의 spot_no
        // MissionService의 로직이 이 좌표(경복궁 근처)에서 true를 반환해야 합니다.
        ArrivalRequest request = new ArrivalRequest(new java.math.BigDecimal("37.579617"), new java.math.BigDecimal("126.977041")); // data.sql에 설정된 경복궁 위경도와 동일

        // when & then
        mockMvc.perform(post("/api/spots/" + spotNo + "/verify-arrival")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // 200 OK 응답을 기대
                .andExpect(jsonPath("$.verified").value(true)) // 응답 본문의 verified 필드가 true인지 확인
                .andExpect(jsonPath("$.message").exists()); // 메시지 필드가 존재하는지 확인
    }

    @Test
    @DisplayName("관광지 도착 검증 실패 테스트 - 실제 위치에서 멀리 떨어짐")
    void verifyArrival_Failure_RealLocationFarFromSpot() throws Exception {
        // given
        Integer spotNo = 1; // data.sql에 있는 '경복궁'의 spot_no
        // 이 위도, 경도는 spotNo 1번과 멀리 떨어져 있어 MissionService에서 false를 반환해야 합니다.
        ArrivalRequest request = new ArrivalRequest(new java.math.BigDecimal("33.5145"), new java.math.BigDecimal("126.5458")); // 제주도 위도/경도

        // when & then
        mockMvc.perform(post("/api/spots/" + spotNo + "/verify-arrival")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // MissionService에서 false 반환 시 400 Bad Request 기대
                .andExpect(jsonPath("$.verified").value(false)) // 응답 본문의 verified 필드가 false인지 확인
                .andExpect(jsonPath("$.message").exists()); // 메시지 필드가 존재하는지 확인
    }
}
