package com.mytripquest.domain.quest.dto;

import com.mytripquest.domain.quest.entity.Difficulty;
import com.mytripquest.domain.quest.entity.QuestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestInfoWithStatusDto {
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
    private QuestStatus status;
}
