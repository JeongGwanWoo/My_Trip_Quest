package com.mytripquest.domain.ranking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRankDto {
    private long rank;
    private String nickname;
    private int points;
    private int totalXp;
    private int level;
}
