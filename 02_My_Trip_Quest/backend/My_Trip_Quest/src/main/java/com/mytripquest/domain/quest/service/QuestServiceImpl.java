package com.mytripquest.domain.quest.service;

import com.mytripquest.domain.quest.dto.LocationWithQuestCountDto;
import com.mytripquest.domain.quest.dto.UserAreaQuestStatusDto;
import com.mytripquest.domain.quest.entity.Quest;
import com.mytripquest.domain.quest.entity.QuestStatus;
import com.mytripquest.domain.quest.entity.UserQuest;
import com.mytripquest.domain.quest.repository.QuestRepository;
import com.mytripquest.domain.quest.repository.UserQuestRepository;
import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 퀘스트 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Service
@Transactional
@RequiredArgsConstructor
public class QuestServiceImpl implements QuestService {

    private final QuestRepository questRepository;
    private final UserQuestRepository userQuestRepository;
    // MVP 단계에서는 지역 정보를 하드코딩하여 사용
    private static final Map<String, String> AREA_CODES;
    private static final Map<String, String> CODE_TO_NAME;


    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("서울특별시", "1");
        aMap.put("광주광역시", "5");
        AREA_CODES = Collections.unmodifiableMap(aMap);
        CODE_TO_NAME = Collections.unmodifiableMap(aMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
    }

    /**
     * 각 지역별 퀘스트 개수와 사용자의 완료 현황을 조회합니다.
     * @param userId 현재 사용자의 ID
     * @return 각 지역의 이름, 총 퀘스트 개수, 완료한 퀘스트 개수를 담은 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserAreaQuestStatusDto> getUserAreaQuestCounts(Long userId) {
        List<UserAreaQuestStatusDto> areaQuestStatus = new ArrayList<>();
        for (Map.Entry<String, String> entry : AREA_CODES.entrySet()) {
            String areaName = entry.getKey();
            String areaCode = entry.getValue();

            // 1. 해당 지역의 전체 관광지 수 계산
            int totalLocations = questRepository.countTotalLocationsByArea(areaCode);

            // 2. 해당 지역에서 사용자가 완료하지 않은 관광지 수 계산
            int incompleteLocations = userQuestRepository.countIncompleteLocationsByArea(userId, areaCode);

            // 3. DTO 생성
            areaQuestStatus.add(UserAreaQuestStatusDto.builder()
                    .areaName(areaName)
                    .areaCode(areaCode)
                    .incompleteLocationCount(incompleteLocations)
                    .totalLocationCount(totalLocations)
                    .build());
        }
        return areaQuestStatus;
    }

    /**
     * 지역 코드를 받아 해당 지역의 퀘스트가 있는 관광지 목록을 조회합니다.
     * @param areaCode (1, 5 등)
     * @return 해당 지역의 관광지 정보와 퀘스트 개수를 담은 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<LocationWithQuestCountDto> getLocationsByAreaCode(String areaCode) {
        if (!CODE_TO_NAME.containsKey(areaCode)) {
            return Collections.emptyList();
        }
        return questRepository.findLocationsByAreaCode(areaCode);
    }

    /**
     * 특정 관광지 ID를 기준으로 해당 관광지에 속한 퀘스트 목록을 조회합니다.
     * @param locationId
     * @return 퀘스트 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<Quest> getQuestsByLocationId(Long locationId) {
        return questRepository.findQuestsByLocationId(locationId);
    }

    /**
     * 사용자가 퀘스트를 수락하는 로직을 처리합니다.
     * @param questId 수락할 퀘스트 ID
     * @param userId 수락하는 사용자 ID
     */
    @Override
    public void acceptQuest(long questId, long userId) {
        // 1. 퀘스트 정보 조회
        Quest quest = questRepository.findQuestById(questId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUEST_NOT_FOUND));

        // 2. 이미 수락한 퀘스트인지 확인
        userQuestRepository.findByUserIdAndQuestId(userId, questId).ifPresent(userQuest -> {
            throw new BusinessException(ErrorCode.QUEST_ALREADY_ACCEPTED);
        });

        // 3. 선행 퀘스트 조건 확인
        if (quest.getPreviousQuestId() != null && quest.getPreviousQuestId() > 0) {
            userQuestRepository.findCompletedByUserIdAndQuestId(userId, quest.getPreviousQuestId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PREVIOUS_QUEST_NOT_COMPLETED));
        }

        // 4. 퀘스트 수락 처리
        UserQuest newUserQuest = UserQuest.builder()
                .userId(userId)
                .questId(questId)
                .status(QuestStatus.ACCEPTED)
                .build();
        userQuestRepository.save(newUserQuest);
    }
}
