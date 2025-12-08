package com.mytripquest.domain.ranking.service;

import com.mytripquest.domain.ranking.dto.RankingInfoResponseDto;
import com.mytripquest.domain.ranking.dto.UserRankDto;

import java.util.List;

public interface RankingService {
    List<RankingInfoResponseDto> getGlobalRankings(int limit);
    UserRankDto getMyRank(long userId);
}
