package com.mytripquest.domain.quest.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quest {
    private long questId;
    private Long locationId;
    private int questTypeId;
    private Long previousQuestId;
    private Difficulty difficulty;
    private String title;
    private String description;
    private int rewardXp;
    private int rewardPoints;
    private boolean requireGpsVerify;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
