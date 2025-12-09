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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        User user = User.builder().email("test@example.com").nickname("tester").passwordHash("password").role("USER").build();
        userMapper.save(user);
        testUser = userMapper.findByEmail("test@example.com").get(); // save 후 ID가 채워진 객체를 다시 조회

        // 2. JWT 토큰 생성
        testToken = jwtTokenProvider.createToken(testUser.getEmail(), testUser.getRole());

        // 3. 테스트 퀘스트 조회
        // 'quest_dummy.sql'에 ID=1000, quest_type_id=1인 '도착' 퀘스트가 있다고 가정
        arrivalQuest = questRepository.findQuestById(1000L).get();

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
        // 'quest_dummy.sql'에서 ID 1000번 퀘스트의 장소(location_id=1) 좌표가 (37.566826, 126.9786567)라고 가정
        QuestCompleteRequestDto requestDto = new QuestCompleteRequestDto(37.5668, 126.9786);
        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when: '퀘스트 완료' API를 호출하면
        mockMvc.perform(post("/api/v1/quest-map/quests/" + arrivalQuest.getQuestId() + "/complete")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // then: HTTP 200 OK 응답을 받는다.
                .andExpect(status().isOk());

        // then: DB의 퀘스트 상태가 COMPLETED로 변경되었는지 확인한다.
        UserQuest completedUserQuest = userQuestRepository.findByUserIdAndQuestId(testUser.getUserId(), arrivalQuest.getQuestId()).get();
        assertEquals(QuestStatus.COMPLETED, completedUserQuest.getStatus());
    }

    @Test
    @DisplayName("도착 퀘스트 완료 실패 - 거리가 너무 멈")
    void completeArrivalQuest_Fail_DistanceTooFar() throws Exception {
        // given: 사용자의 위치가 퀘스트 장소와 멀 때
        QuestCompleteRequestDto requestDto = new QuestCompleteRequestDto(38.0, 128.0);
        String requestJson = objectMapper.writeValueAsString(requestDto);

        // when: '퀘스트 완료' API를 호출하면
        mockMvc.perform(post("/api/v1/quest-map/quests/" + arrivalQuest.getQuestId() + "/complete")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // then: HTTP 400 Bad Request 응답과 에러 코드를 받는다.
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("DISTANCE_TOO_FAR"));
    }

    // TODO: 퀘스트 상태가 올바르지 않은 경우 (이미 완료, 수락 안 함) 테스트 추가
    // TODO: 좌표 값이 누락된 경우 테스트 추가
}
