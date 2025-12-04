package com.mytripquest.controller;

import com.mytripquest.domain.quest.dto.AreaQuestCountDto;
import com.mytripquest.domain.quest.entity.Quest;
import com.mytripquest.domain.quest.service.QuestService;
import com.mytripquest.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 퀘스트 관련 API 컨트롤러
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/quest-map")
public class QuestController {

    private final QuestService questService;

    /**
     * 지도에 표시될 지역별 퀘스트 개수를 조회합니다.
     * @return 각 지역의 이름과 퀘스트 개수를 담은 DTO 리스트
     */
    @GetMapping("/areas")
    public ResponseEntity<ApiResponse<List<AreaQuestCountDto>>> getAreas() {
        List<AreaQuestCountDto> areaQuestCounts = questService.getAreaQuestCounts();
        return ResponseEntity.ok(ApiResponse.success(areaQuestCounts));
    }

    /**
     * 특정 지역에 속한 모든 퀘스트의 상세 목록을 조회합니다.
     * @param areaName (seoul, gwangju 등)
     * @return 해당 지역의 퀘스트 리스트
     */
    @GetMapping("/areas/{areaName}")
    public ResponseEntity<ApiResponse<List<Quest>>> getQuestsByArea(@PathVariable String areaName) {
        List<Quest> quests = questService.getQuestsByAreaCode(areaName);
        return ResponseEntity.ok(ApiResponse.success(quests));
    }
}
