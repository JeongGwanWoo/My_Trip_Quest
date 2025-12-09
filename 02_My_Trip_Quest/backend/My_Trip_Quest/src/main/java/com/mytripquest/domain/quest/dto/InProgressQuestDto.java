package com.mytripquest.domain.quest.dto;

import com.mytripquest.domain.quest.entity.QuestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InProgressQuestDto {
    private final Long questId;
    private final String title;
    private final QuestStatus status;
    private final LocalDateTime acceptedAt;
    private final String locationName;

    @Builder
    public InProgressQuestDto(Long questId, String title, QuestStatus status, LocalDateTime acceptedAt, String locationName) {
        this.questId = questId;
        this.title = title;
        this.status = status;
        this.acceptedAt = acceptedAt;
        this.locationName = locationName;
    }
}
