package com.mytripquest.domain.activitylog.dto;

import com.mytripquest.domain.activitylog.entity.ActivityLog;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ActivityLogResponseDto {
    private Long logId;
    private String activityType;
    private Long relatedId;
    private String logMessage;
    private LocalDateTime createdAt;

    @Builder
    public ActivityLogResponseDto(ActivityLog log) {
        this.logId = log.getId();
        this.activityType = log.getActivityType();
        this.relatedId = log.getRelatedId();
        this.logMessage = log.getLogMessage();
        this.createdAt = log.getCreatedAt();
    }
}
