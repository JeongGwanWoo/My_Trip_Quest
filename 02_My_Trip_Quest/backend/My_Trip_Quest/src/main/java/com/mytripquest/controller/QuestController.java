package com.mytripquest.controller;

import com.mytripquest.domain.quest.dto.InProgressQuestDto;
import com.mytripquest.domain.quest.dto.QuestCompleteRequestDto;
import com.mytripquest.domain.quest.dto.UserAreaQuestStatusDto;
import com.mytripquest.domain.quest.dto.LocationWithQuestCountDto;
import com.mytripquest.domain.quest.dto.QuestAcceptRequestDto;
import com.mytripquest.domain.quest.entity.Quest;
import com.mytripquest.domain.quest.service.QuestService;
import com.mytripquest.domain.user.repository.UserMapper;
import com.mytripquest.global.ApiResponse;
import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserMapper userMapper;

    // TODO: CustomUserDetails 구현 후 @AuthenticationPrincipal 사용하도록 변경 필요
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED); // 또는 적절한 예외 처리
        }
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userMapper.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)) // 또는 적절한 예외 처리
                .getUserId();
    }

    /**
     * 지도에 표시될 지역별 퀘스트 개수와 현재 사용자의 완료 현황을 조회합니다.
     * @param userId 현재 조회하는 사용자의 ID
     * @return 각 지역의 이름, 총 퀘스트 개수, 완료한 퀘스트 개수를 담은 DTO 리스트
     */
    @GetMapping("/areas")
    public ResponseEntity<ApiResponse<List<UserAreaQuestStatusDto>>> getAreas() {
        Long userId = getCurrentUserId();
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
    public ResponseEntity<ApiResponse<Void>> acceptQuest(@PathVariable long questId) {
        Long userId = getCurrentUserId();
        questService.acceptQuest(questId, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 퀘스트를 완료 처리합니다.
     * @param questId 완료할 퀘스트의 ID
     * @return 성공 응답
     */
    @PostMapping("/quests/{questId}/complete")
    public ResponseEntity<ApiResponse<Void>> completeQuest(@PathVariable long questId, @RequestBody QuestCompleteRequestDto request) {
        Long userId = getCurrentUserId();
        questService.completeQuest(questId, userId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 현재 사용자가 진행 중인 퀘스트 목록을 조회합니다.
     * @return 진행 중인 퀘스트 목록
     */
    @GetMapping("/quests/in-progress")
    public ResponseEntity<ApiResponse<List<InProgressQuestDto>>> getInProgressQuests() {
        Long userId = getCurrentUserId();
        List<InProgressQuestDto> inProgressQuests = questService.getInProgressQuests(userId);
        return ResponseEntity.ok(ApiResponse.success(inProgressQuests));
    }
}
