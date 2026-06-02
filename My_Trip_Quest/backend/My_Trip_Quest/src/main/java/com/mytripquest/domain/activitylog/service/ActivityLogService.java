package com.mytripquest.domain.activitylog.service;

import com.mytripquest.domain.activitylog.dto.ActivityLogResponseDto;
import com.mytripquest.domain.activitylog.entity.ActivityLog;

import java.util.List;

public interface ActivityLogService {
    void logQuestCompletion(Long userId, Long questId, String questTitle, int rewardXp, int rewardPoints);
    List<ActivityLogResponseDto> getLogsByUserId(Long userId);
}
