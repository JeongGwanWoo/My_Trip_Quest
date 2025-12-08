package com.mytripquest.domain.ranking.service;

import com.mytripquest.domain.ranking.dto.RankingInfoResponseDto;
import com.mytripquest.domain.ranking.dto.UserRankDto;
import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingServiceImpl implements RankingService {

    private final UserMapper userMapper;

    @Override
    public List<RankingInfoResponseDto> getGlobalRankings(int limit) {
        List<User> topUsers = userMapper.findTopUsersByPoints(limit);

        List<RankingInfoResponseDto> rankingList = new java.util.ArrayList<>();
        if (topUsers.isEmpty()) {
            return rankingList;
        }

        long currentRank = 1;
        int previousPoints = topUsers.get(0).getPoints();

        for (int i = 0; i < topUsers.size(); i++) {
            User user = topUsers.get(i);

            if (user.getPoints() < previousPoints) {
                currentRank = i + 1;
            }

            rankingList.add(RankingInfoResponseDto.builder()
                    .rank(currentRank)
                    .nickname(user.getNickname())
                    .points(user.getPoints())
                    .totalXp(user.getTotalXp())
                    .build());
            
            previousPoints = user.getPoints();
        }
        return rankingList;
    }

    @Override
    public UserRankDto getMyRank(long userId) {
        return userMapper.findUserRankById(userId)
                .orElseThrow(() -> new com.mytripquest.global.error.exception.BusinessException(com.mytripquest.global.error.exception.ErrorCode.USER_NOT_FOUND));
    }
}
