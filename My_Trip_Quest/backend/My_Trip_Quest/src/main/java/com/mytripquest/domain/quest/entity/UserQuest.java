package com.mytripquest.domain.quest.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserQuest {
    private long userQuestId;
    private long userId;
    private long questId;
    private QuestStatus status;
    private String proofData;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastUpdatedAt;
}
