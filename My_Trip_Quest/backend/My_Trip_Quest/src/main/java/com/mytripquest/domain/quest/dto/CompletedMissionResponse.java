package com.mytripquest.domain.quest.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CompletedMissionResponse {
    private long questId;
    private String questTitle;
    private String questContent;
    private String locationName;
    private LocalDateTime completedAt;
}
