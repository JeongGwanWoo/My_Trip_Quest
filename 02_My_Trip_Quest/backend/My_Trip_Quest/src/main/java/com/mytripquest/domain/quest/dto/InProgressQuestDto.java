package com.mytripquest.domain.quest.dto;

import com.mytripquest.domain.quest.entity.QuestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InProgressQuestDto {
    private final Long questId;
    private final Long questTypeId;
    private final String title;
    private final String description;
    private final QuestStatus status;
    private final LocalDateTime acceptedAt;
    private final String locationName;
    private final Integer rewardXp;
    private final Integer rewardPoints;

    @Builder
    public InProgressQuestDto(Long questId, Long questTypeId, String title, String description,
                              QuestStatus status, LocalDateTime acceptedAt, String locationName,
                              Integer rewardXp, Integer rewardPoints) {
        this.questId = questId;
        this.questTypeId = questTypeId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.acceptedAt = acceptedAt;
        this.locationName = locationName;
        this.rewardXp = rewardXp;
        this.rewardPoints = rewardPoints;
    }
}
