package com.mytripquest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytripquest.domain.quest.dto.QuestCompleteRequestDto;
import com.mytripquest.domain.quest.entity.Quest;
import com.mytripquest.domain.quest.entity.QuestStatus;
import com.mytripquest.domain.quest.entity.UserQuest;
import com.mytripquest.domain.quest.repository.QuestRepository;
import com.mytripquest.domain.quest.repository.UserQuestRepository;
import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
import com.mytripquest.global.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QuestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestRepository questRepository;

    @Autowired
    private UserQuestRepository userQuestRepository;

    private User testUser;
    private String testToken;
    private Quest arrivalQuest;

        @BeforeEach
        void setUp() {
            // 1. 테스트 사용자 생성 및 ID 확인
            User user = User.builder().email("test@example.com").nickname("tester").passwordHash("password").role(User.Role.USER).build();
            userMapper.save(user);
            testUser = userMapper.findByEmail("test@example.com").get(); // save 후 ID가 채워진 객체를 다시 조회
    
            // 2. JWT 토큰 생성
            testToken = jwtTokenProvider.createToken(testUser.getEmail(), testUser.getRole().name());
    
            // 3. 테스트 퀘스트 조회
            // 'quest_dummy.sql'에 ID=10, quest_type_id=1인 '도착' 퀘스트가 있다고 가정
            arrivalQuest = questRepository.findQuestById(10L).get();
    
            // 4. 사용자가 퀘스트를 수락한 상태로 만듭니다.
            UserQuest userQuest = UserQuest.builder()
                    .userId(testUser.getUserId())
                    .questId(arrivalQuest.getQuestId())
                    .status(QuestStatus.ACCEPTED)
                    .build();
            userQuestRepository.save(userQuest);
        }
    @Test
    @DisplayName("도착 퀘스트 완료 성공")
    void completeArrivalQuest_Success() throws Exception {
        // given: 사용자의 위치가 퀘스트 장소와 가까울 때
        // 'quest_dummy.sql'에서 ID 10번 퀘스트의 장소 좌표가 (37.5796170, 126.9770410)라고 가정
        QuestCompleteRequestDto requestDto = new QuestCompleteRequestDto(37.5796, 126.9770);
        String requestJson = objectMapper.writeValueAsString(requestDto);
        int initialXp = testUser.getTotalXp();
        int initialPoints = testUser.getPoints();

        // when: '퀘스트 완료' API를 호출하면
        mockMvc.perform(post("/api/v1/quest-map/quests/" + arrivalQuest.getQuestId() + "/complete/arrival")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // then: HTTP 200 OK 응답을 받는다.
                .andExpect(status().isOk());

        // then: DB의 퀘스트 상태가 COMPLETED로 변경되었는지 확인한다.
        UserQuest completedUserQuest = userQuestRepository.findByUserIdAndQuestId(testUser.getUserId(), arrivalQuest.getQuestId()).get();
        assertEquals(QuestStatus.COMPLETED, completedUserQuest.getStatus());

        // then: 사용자에게 보상이 정상적으로 지급되었는지 확인한다.
        User rewardedUser = userMapper.findById(testUser.getUserId()).get();
        assertEquals(initialXp + arrivalQuest.getRewardXp(), rewardedUser.getTotalXp());
        assertEquals(initialPoints + arrivalQuest.getRewardPoints(), rewardedUser.getPoints());
    }

    @Test
    @DisplayName("도착 퀘스트 완료 실패 - 거리가 너무 멈")
    void completeArrivalQuest_Fail_DistanceTooFar() throws Exception {
        // given: 사용자의 위치가 퀘스트 장소와 멀 때
        QuestCompleteRequestDto requestDto = new QuestCompleteRequestDto(38.0, 128.0);
        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when: '퀘스트 완료' API를 호출하면
        mockMvc.perform(post("/api/v1/quest-map/quests/" + arrivalQuest.getQuestId() + "/complete/arrival")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // then: BusinessException이 발생하고, 그 원인이 DISTANCE_TOO_FAR인지 확인한다.
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals(ErrorCode.DISTANCE_TOO_FAR.getMessage(), result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("도착 퀘스트 완료 실패 - 좌표 값 누락")
    void completeArrivalQuest_Fail_MissingCoordinates() throws Exception {
        // given: 위도 또는 경도 값이 누락된 요청
        String requestJson = "{\"latitude\": 37.5796}"; // 경도 누락

        // when: '퀘스트 완료' API를 호출하면
        mockMvc.perform(post("/api/v1/quest-map/quests/" + arrivalQuest.getQuestId() + "/complete/arrival")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // then: BusinessException이 발생하고, 그 원인이 GPS_COORDINATES_REQUIRED인지 확인한다.
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals(ErrorCode.GPS_COORDINATES_REQUIRED.getMessage(), result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("도착 퀘스트 완료 실패 - 이미 완료된 퀘스트")
    void completeArrivalQuest_Fail_AlreadyCompleted() throws Exception {
        // given: 퀘스트를 이미 완료한 상태
        UserQuest completedUserQuest = userQuestRepository.findByUserIdAndQuestId(testUser.getUserId(), arrivalQuest.getQuestId()).get();
        completedUserQuest.setStatus(QuestStatus.COMPLETED);
        userQuestRepository.update(completedUserQuest); // DB에 업데이트

        QuestCompleteRequestDto requestDto = new QuestCompleteRequestDto(37.5796, 126.9770);
        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when: '퀘스트 완료' API를 호출하면
        mockMvc.perform(post("/api/v1/quest-map/quests/" + arrivalQuest.getQuestId() + "/complete/arrival")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // then: BusinessException이 발생하고, 그 원인이 QUEST_ALREADY_COMPLETED인지 확인한다.
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals(ErrorCode.QUEST_ALREADY_COMPLETED.getMessage(), result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("도착 퀘스트 완료 실패 - 수락하지 않은 퀘스트")
    void completeArrivalQuest_Fail_NotAccepted() throws Exception {
        // given: 퀘스트를 수락하지 않은 상태
        // setUp에서 수락된 퀘스트를 삭제하여 수락되지 않은 상태로 만듦
        // (주의: @Transactional이므로 이 테스트에만 영향을 줌)
        userQuestRepository.findByUserIdAndQuestId(testUser.getUserId(), arrivalQuest.getQuestId())
                .ifPresent(userQuestRepository::delete); // UserQuestRepository에 delete 메서드가 있다고 가정

        QuestCompleteRequestDto requestDto = new QuestCompleteRequestDto(37.5796, 126.9770);
        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when: '퀘스트 완료' API를 호출하면
        mockMvc.perform(post("/api/v1/quest-map/quests/" + arrivalQuest.getQuestId() + "/complete/arrival")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // then: BusinessException이 발생하고, 그 원인이 QUEST_NOT_ACCEPTED인지 확인한다.
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException))
                .andExpect(result -> assertEquals(ErrorCode.QUEST_NOT_ACCEPTED.getMessage(), result.getResolvedException().getMessage()));
    }
    
    @Disabled // 실제 Gemini API를 호출하므로, CI/CD 환경에서는 실행하지 않도록 비활성화합니다. 로컬에서 수동으로 실행하여 확인할 수 있습니다.
    @Test
    @DisplayName("사진 퀘스트 완료 성공 (실제 API 호출)")
    void completePhotoQuest_Success() throws Exception {
        // given: '사진 퀘스트'를 수락한 상태로 만듭니다.
        // 'quest_dummy.sql'에 ID=11, quest_type_id=2인 '사진' 퀘스트가 있다고 가정
        long photoQuestId = 11L;
        UserQuest photoUserQuest = UserQuest.builder()
                .userId(testUser.getUserId())
                .questId(photoQuestId)
                .status(QuestStatus.ACCEPTED)
                .build();
        userQuestRepository.save(photoUserQuest);

        // given: 검증에 성공할 만한 (가짜) 이미지 파일을 준비합니다.
        MockMultipartFile imageFile = new MockMultipartFile(
                "image", // 컨트롤러에서 받을 @RequestParam("image")의 이름과 일치
                "landmark.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-bytes".getBytes() // 실제로는 src/test/resources에 있는 테스트용 이미지 파일을 읽어와 사용합니다.
        );

        // when: '사진 퀘스트 완료' API를 호출하면
        mockMvc.perform(multipart("/api/v1/quest-map/quests/" + photoQuestId + "/complete/photo")
                        .file(imageFile)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                // then: HTTP 200 OK 응답을 받는다.
                .andExpect(status().isOk());

        // then: DB의 퀘스트 상태가 COMPLETED로 변경되었는지 확인한다.
        UserQuest completedUserQuest = userQuestRepository.findByUserIdAndQuestId(testUser.getUserId(), photoQuestId).get();
        assertEquals(QuestStatus.COMPLETED, completedUserQuest.getStatus());
    }
}
