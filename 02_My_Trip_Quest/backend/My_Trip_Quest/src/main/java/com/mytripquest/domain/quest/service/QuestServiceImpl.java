package com.mytripquest.domain.quest.service;

import com.mytripquest.domain.quest.dto.AreaQuestCountDto;
import com.mytripquest.domain.quest.entity.Quest;
import com.mytripquest.domain.quest.repository.QuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 퀘스트 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class QuestServiceImpl implements QuestService {

    private final QuestRepository questRepository;
    // MVP 단계에서는 지역 정보를 하드코딩하여 사용
    private static final Map<String, String> AREA_CODES;

    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("seoul", "1");
        aMap.put("gwangju", "5");
        AREA_CODES = Collections.unmodifiableMap(aMap);
    }

    /**
     * 각 지역별 퀘스트 개수를 조회합니다.
     * @return 각 지역의 이름과 퀘스트 개수를 담은 DTO 리스트
     */
    @Override
    public List<AreaQuestCountDto> getAreaQuestCounts() {
        List<AreaQuestCountDto> areaQuestCounts = new ArrayList<>();
        for (Map.Entry<String, String> entry : AREA_CODES.entrySet()) {
            List<Quest> quests = questRepository.findByAreaCode(entry.getValue());
            areaQuestCounts.add(AreaQuestCountDto.builder()
                    .areaName(entry.getKey())
                    .questCount(quests.size())
                    .build());
        }
        return areaQuestCounts;
    }

    /**
     * 지역 이름(영문)을 받아 해당 지역의 퀘스트 목록을 조회합니다.
     * @param areaName (seoul, gwangju 등)
     * @return 해당 지역의 퀘스트 리스트
     */
    @Override
    public List<Quest> getQuestsByAreaCode(String areaName) {
        String areaCode = AREA_CODES.get(areaName.toLowerCase());
        if (areaCode == null) {
            return Collections.emptyList();
        }
        return questRepository.findByAreaCode(areaCode);
    }
}
