package com.ssafy.tripquest.controller;

import com.ssafy.tripquest.config.TestConfig;
import com.ssafy.tripquest.service.AIVisionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Import(TestConfig.class)
public class MissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AIVisionService aiVisionService;

    @Test
    @DisplayName("경복궁 사진 미션 성공 (위치 파라미터 제공, AI 검증 통과)")
    void gyeongbokgungPhoto_AiPasses_WhenLocationIsProvided() throws Exception {
        // given
        // 1. AI 검증 서비스가 '경복궁'에 대해 'true'를 반환하도록 설정
        // any(byte[].class) : 이미지 바이트는 어떤 것이든 상관없이
        // eq("경복궁") : 랜드마크 이름이 '경복궁'일 경우
        when(aiVisionService.isPhotoOfLandmark(any(byte[].class), eq("경복궁"))).thenReturn(true);

        // 2. 테스트용 이미지 파일 로드
        ClassPathResource resource = new ClassPathResource("img_test/경복궁.jpg");
        MockMultipartFile imageFile = new MockMultipartFile("file", "경복궁.jpg", MediaType.IMAGE_JPEG_VALUE, resource.getInputStream());

        // 3. 미션 정보 및 위치 정보 설정
        Long missionId = 3L; // '근정전 사진 찍기' 미션
        Long userId = 1L;
        String gyeongbokgungLat = "37.579617"; // 경복궁 실제 위도
        String gyeongbokgungLon = "126.977041"; // 경복궁 실제 경도

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/missions/photo/" + missionId)
                        .file(imageFile)
                        .param("userId", String.valueOf(userId))
                        .param("latitude", gyeongbokgungLat) // 메타데이터 대신 현재 위치 전달
                        .param("longitude", gyeongbokgungLon))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("사진 인증에 성공했습니다. 포인트를 획득했습니다!"));
    }

    @Test
    @DisplayName("창덕궁 사진으로 경복궁 미션 실패 (위치 파라미터 제공, AI 검증 실패)")
    void changdeokgungPhoto_AiFails_ForGyeongbokgungMission() throws Exception {
        // given
        // 1. AI 검증 서비스가 '경복궁'에 대해 'false'를 반환하도록 설정
        when(aiVisionService.isPhotoOfLandmark(any(byte[].class), eq("경복궁"))).thenReturn(false);

        // 2. 테스트용 '창덕궁' 이미지 파일 로드
        ClassPathResource resource = new ClassPathResource("img_test/창덕궁.jpg");
        MockMultipartFile imageFile = new MockMultipartFile("file", "창덕궁.jpg", MediaType.IMAGE_JPEG_VALUE, resource.getInputStream());

        // 3. 미션 정보 및 위치 정보 설정
        Long missionId = 3L; // 여전히 '근정전 사진 찍기' 미션
        Long userId = 1L;
        String gyeongbokgungLat = "37.579617"; // 위치는 경복궁으로 통과
        String gyeongbokgungLon = "126.977041";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/missions/photo/" + missionId)
                        .file(imageFile)
                        .param("userId", String.valueOf(userId))
                        .param("latitude", gyeongbokgungLat)
                        .param("longitude", gyeongbokgungLon))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("사진 내용이 미션 장소와 일치하지 않습니다."));
    }
}
