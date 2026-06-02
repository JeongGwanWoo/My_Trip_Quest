package com.mytripquest.domain.user.util;

import com.mytripquest.domain.user.dto.LevelProgressDto;

public class LevelUtil {

    /**
     * 총 경험치를 기반으로 사용자의 현재 레벨을 계산합니다.
     * 레벨 1 -> 2: 100 XP
     * 레벨 2 -> 3: 110 XP
     * ...
     * 각 레벨업에 필요한 경험치가 10씩 증가합니다.
     * 최대 레벨은 99입니다.
     *
     * @param totalXp 총 경험치
     * @return 계산된 현재 레벨
     */
    public static int calculateLevel(int totalXp) {
        if (totalXp < 100) {
            return 1;
        }

        int level = 1;
        int cumulativeXpToReachNextLevel = 0; // 이 레벨에 도달하기 위한 누적 XP
        int xpNeededForCurrentLevelUp = 100; // 현재 레벨에서 다음 레벨로 가기 위한 XP

        while (level < 99) {
            cumulativeXpToReachNextLevel += xpNeededForCurrentLevelUp;
            if (totalXp < cumulativeXpToReachNextLevel) {
                return level;
            }
            level++;
            xpNeededForCurrentLevelUp += 10;
        }

        return 99; // Max level reached
    }

    /**
     * 특정 레벨을 달성하기 위해 필요한 누적 경험치를 계산합니다.
     *
     * @param level 목표 레벨
     * @return 해당 레벨을 달성하기 위한 총 경험치. 레벨 1은 0.
     */
    public static int getTotalXpForLevel(int level) {
        if (level <= 1) {
            return 0;
        }
        if (level >= 99) { // Max level is 99
            // Calculate total XP needed to reach level 99
            int cumulativeXp = 0;
            int xpNeeded = 100;
            for (int l = 1; l < 99; l++) {
                cumulativeXp += xpNeeded;
                xpNeeded += 10;
            }
            return cumulativeXp;
        }

        int cumulativeXp = 0;
        int xpNeeded = 100; // 레벨 1 -> 2
        for (int l = 1; l < level; l++) {
            cumulativeXp += xpNeeded;
            xpNeeded += 10;
        }
        return cumulativeXp;
    }

    /**
     * 총 경험치를 기반으로 현재 레벨 진행 상태 정보를 반환합니다.
     *
     * @param totalXp 사용자의 총 경험치
     * @return LevelProgressDto 객체
     */
    public static LevelProgressDto getLevelProgress(int totalXp) {
        int currentLevel = calculateLevel(totalXp);
        
        int xpForCurrentLevel = getTotalXpForLevel(currentLevel);
        int xpForNextLevel = getTotalXpForLevel(currentLevel + 1);
        
        // If currentLevel is max level, set nextLevel XP to currentLevel XP
        if (currentLevel >= 99) {
            xpForNextLevel = xpForCurrentLevel; 
        }

        return LevelProgressDto.builder()
                .currentLevel(currentLevel)
                .totalXp(totalXp)
                .xpForCurrentLevel(xpForCurrentLevel)
                .xpForNextLevel(xpForNextLevel)
                .build();
    }
}
