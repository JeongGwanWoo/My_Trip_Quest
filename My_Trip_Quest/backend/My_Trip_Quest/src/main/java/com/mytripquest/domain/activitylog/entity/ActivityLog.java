package com.mytripquest.domain.activitylog.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ActivityLog {

    private Long id;
    private Long userId;
    private String activityType;
    private Long relatedId;
    private String logMessage;
    private LocalDateTime createdAt;

    @Builder
    public ActivityLog(Long userId, String activityType, Long relatedId, String logMessage) {
        this.userId = userId;
        this.activityType = activityType;
        this.relatedId = relatedId;
        this.logMessage = logMessage;
    }
}
