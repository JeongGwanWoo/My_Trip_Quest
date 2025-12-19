package com.mytripquest.domain.activitylog.service;

import com.mytripquest.domain.activitylog.dto.ActivityLogResponseDto;
import com.mytripquest.domain.activitylog.entity.ActivityLog;
import com.mytripquest.domain.activitylog.repository.ActivityLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogMapper activityLogMapper;
    private static final String QUEST_COMPLETION_TYPE = "QUEST_COMPLETED";

    @Override
    public void logQuestCompletion(Long userId, Long questId, String questTitle, int rewardXp, int rewardPoints) {
        String message = String.format("퀘스트 '%s'를 완료하여 %dXP와 %d포인트를 획득했습니다.", questTitle, rewardXp, rewardPoints);
        ActivityLog log = ActivityLog.builder()
                .userId(userId)
                .activityType(QUEST_COMPLETION_TYPE)
                .relatedId(questId)
                .logMessage(message)
                .build();
        activityLogMapper.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityLogResponseDto> getLogsByUserId(Long userId) {
        return activityLogMapper.findByUserId(userId);
    }
}

