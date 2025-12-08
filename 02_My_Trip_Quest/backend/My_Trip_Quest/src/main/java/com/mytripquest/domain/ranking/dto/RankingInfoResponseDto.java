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
    
    // TODO: XP를 레벨로 변환하는 로직 추가
    public int getLevel() {
        // 임시 레벨 계산 로직: 1000xp 마다 1레벨
        return 1 + (this.totalXp / 1000);
    }
}
