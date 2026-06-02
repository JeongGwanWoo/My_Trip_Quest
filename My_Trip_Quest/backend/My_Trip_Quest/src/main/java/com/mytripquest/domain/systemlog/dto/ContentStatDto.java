package com.mytripquest.domain.systemlog.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
public class ContentStatDto {
    private List<UserLevelStat> userLevelStats;
    private List<RegionalStat> regionalStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserLevelStat {
        private String levelRange; // "1-10", "11-20" ...
        private int userCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegionalStat {
        private String areaName; // 서울, 경기 ...
        private int completedCount;
    }
}
