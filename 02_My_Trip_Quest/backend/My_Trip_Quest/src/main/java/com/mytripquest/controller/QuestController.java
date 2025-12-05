package com.mytripquest.controller;

import com.mytripquest.domain.quest.dto.UserAreaQuestStatusDto;
import com.mytripquest.domain.quest.dto.LocationWithQuestCountDto;
import com.mytripquest.domain.quest.dto.QuestAcceptRequestDto;
import com.mytripquest.domain.quest.entity.Quest;
import com.mytripquest.domain.quest.service.QuestService;
import com.mytripquest.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * 지도에 표시될 지역별 퀘스트 개수와 현재 사용자의 완료 현황을 조회합니다.
     * @param userId 현재 조회하는 사용자의 ID
     * @return 각 지역의 이름, 총 퀘스트 개수, 완료한 퀘스트 개수를 담은 DTO 리스트
     */
    // TODO: JWT 인증 구현 후, @RequestParam 대신 @AuthenticationPrincipal 사용하여 사용자 정보를 가져와야 합니다.
    // 현재는 클라이언트에서 userId를 직접 받는 임시 방편이며, 이는 보안상 취약합니다.
    @GetMapping("/areas")
    public ResponseEntity<ApiResponse<List<UserAreaQuestStatusDto>>> getAreas(@RequestParam Long userId) {
        List<UserAreaQuestStatusDto> areaQuestStatus = questService.getUserAreaQuestCounts(userId);
        return ResponseEntity.ok(ApiResponse.success(areaQuestStatus));
    }

    /**
     * 특정 지역에 속한, 퀘스트가 있는 관광지 목록을 조회합니다.
     * @param areaCode (1, 5 등)
     * @return 해당 지역의 관광지 정보와 퀘스트 개수를 담은 DTO 리스트
     */
    @GetMapping("/areas/{areaCode}")
    public ResponseEntity<ApiResponse<List<LocationWithQuestCountDto>>> getLocationsByArea(@PathVariable String areaCode) {
        List<LocationWithQuestCountDto> locations = questService.getLocationsByAreaCode(areaCode);
        return ResponseEntity.ok(ApiResponse.success(locations));
    }

    /**
     * 특정 관광지 ID에 속한 모든 퀘스트의 상세 목록을 조회합니다.
     * @param locationId
     * @return 해당 관광지의 퀘스트 리스트
     */
    @GetMapping("/locations/{locationId}")
    public ResponseEntity<ApiResponse<List<Quest>>> getQuestsByLocation(@PathVariable Long locationId) {
        List<Quest> quests = questService.getQuestsByLocationId(locationId);
        return ResponseEntity.ok(ApiResponse.success(quests));
    }

    /**
     * 퀘스트를 수락합니다.
     * @param questId 수락할 퀘스트의 ID
     * @param request 수락 요청 DTO (사용자 ID 포함)
     * @return 성공 응답
     */
    @PostMapping("/quests/{questId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptQuest(@PathVariable long questId, @RequestBody QuestAcceptRequestDto request) {
        questService.acceptQuest(questId, request.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
