package com.mytripquest.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LevelProgressDto {
    private int currentLevel;
    private int totalXp;
    private int xpForCurrentLevel; // 현재 레벨을 달성하는 데 필요한 총 XP
    private int xpForNextLevel; // 다음 레벨을 달성하는 데 필요한 총 XP

    /**
     * 현재 레벨에서 쌓은 경험치
     */
    public int getXpInCurrentLevel() {
        return totalXp - xpForCurrentLevel;
    }

    /**
     * 다음 레벨까지 필요한 경험치
     */
    public int getXpNeededForLevelUp() {
        return xpForNextLevel - xpForCurrentLevel;
    }

    /**
     * 현재 레벨 진행률 (퍼센트)
     */
    public int getProgressPercentage() {
        int needed = getXpNeededForLevelUp();
        if (needed <= 0) {
            return 100; // Max level or no XP needed
        }
        int current = getXpInCurrentLevel();
        return (int) Math.floor(((double) current / needed) * 100);
    }
}
