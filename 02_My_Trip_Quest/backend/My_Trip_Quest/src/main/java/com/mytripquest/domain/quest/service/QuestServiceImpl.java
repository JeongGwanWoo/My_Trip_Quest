package com.mytripquest.domain.quest.service;

import com.mytripquest.domain.quest.dto.AreaQuestCountDto;
import com.mytripquest.domain.quest.dto.LocationWithQuestCountDto;
import com.mytripquest.domain.quest.entity.Quest;
import com.mytripquest.domain.quest.repository.QuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
@RequiredArgsConstructor
public class QuestServiceImpl implements QuestService {

    private final QuestRepository questRepository;
    // MVP 단계에서는 지역 정보를 하드코딩하여 사용
    private static final Map<String, String> AREA_CODES;
    private static final Map<String, String> CODE_TO_NAME;


    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("seoul", "1");
        aMap.put("gwangju", "5");
        AREA_CODES = Collections.unmodifiableMap(aMap);
        CODE_TO_NAME = Collections.unmodifiableMap(aMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
    }

    /**
     * 각 지역별 퀘스트 개수를 조회합니다.
     * @return 각 지역의 이름과 퀘스트 개수를 담은 DTO 리스트
     */
    @Override
    public List<AreaQuestCountDto> getAreaQuestCounts() {
        List<AreaQuestCountDto> areaQuestCounts = new ArrayList<>();
        for (Map.Entry<String, String> entry : AREA_CODES.entrySet()) {
            List<LocationWithQuestCountDto> locations = questRepository.findLocationsByAreaCode(entry.getValue());
            int totalQuests = locations.stream().mapToInt(LocationWithQuestCountDto::getQuestCount).sum();
            areaQuestCounts.add(AreaQuestCountDto.builder()
                    .areaName(entry.getKey())
                    .questCount(totalQuests)
                    .build());
        }
        return areaQuestCounts;
    }

    /**
     * 지역 코드를 받아 해당 지역의 퀘스트가 있는 관광지 목록을 조회합니다.
     * @param areaCode (1, 5 등)
     * @return 해당 지역의 관광지 정보와 퀘스트 개수를 담은 DTO 리스트
     */
    @Override
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
    public List<Quest> getQuestsByLocationId(Long locationId) {
        return questRepository.findQuestsByLocationId(locationId);
    }
}
