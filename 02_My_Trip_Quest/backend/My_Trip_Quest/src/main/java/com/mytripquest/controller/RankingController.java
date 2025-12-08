package com.mytripquest.controller;

import com.mytripquest.domain.ranking.dto.RankingInfoResponseDto;
import com.mytripquest.domain.ranking.service.RankingService;
import com.mytripquest.global.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 랭킹 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    /**
     * 글로벌 랭킹 목록을 조회합니다.
     * @param limit 조회할 랭킹 개수 (기본값 10)
     * @return 랭킹 정보를 담은 DTO 리스트
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RankingInfoResponseDto>>> getGlobalRankings(
            @RequestParam(defaultValue = "10") int limit) {
        List<RankingInfoResponseDto> rankings = rankingService.getGlobalRankings(limit);
        return ResponseEntity.ok(ApiResponse.success(rankings));
    }

    /**
     * 현재 로그인한 사용자의 랭킹 정보를 조회합니다.
     * @param userId 현재 사용자의 ID (TODO: @AuthenticationPrincipal 로 대체)
     * @return 사용자의 랭킹 정보를 담은 DTO
     */
    @GetMapping("/my-rank")
    public ResponseEntity<ApiResponse<com.mytripquest.domain.ranking.dto.UserRankDto>> getMyRank(@RequestParam long userId) {
        com.mytripquest.domain.ranking.dto.UserRankDto myRank = rankingService.getMyRank(userId);
        return ResponseEntity.ok(ApiResponse.success(myRank));
    }
}
