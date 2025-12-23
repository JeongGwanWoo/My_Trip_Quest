package com.mytripquest.domain.ranking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RankingInfoResponseDto {
    private long rank;
    private String nickname;
    private int points;
    private int totalXp;
    private int level;
}
