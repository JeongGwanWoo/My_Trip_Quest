package com.mytripquest.domain.systemlog.service;

import com.mytripquest.domain.systemlog.dto.SystemLogDto;
import com.mytripquest.domain.systemlog.mapper.SystemLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemLogService {

    private final SystemLogMapper systemLogMapper;
    private final com.mytripquest.domain.user.repository.UserMapper userMapper;
    private final com.mytripquest.domain.quest.repository.UserQuestRepository userQuestRepository;

    /**
     * 비동기 로그 저장
     * 메인 트랜잭션과 분리되어 실행됨 (예외 발생 시에도 메인 로직 영향 없음)
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordLog(SystemLogDto logDto) {
        systemLogMapper.insertLog(logDto);
    }

    /**
     * 관리자 대시보드 통계 조회
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("errorRates", systemLogMapper.selectErrorRateStats());
        stats.put("slowestFeatures", systemLogMapper.selectSlowestFeatures());
        stats.put("traffic", systemLogMapper.selectTrafficStats());
        return stats;
    }

    @Transactional(readOnly = true)
    public com.mytripquest.domain.systemlog.dto.EconomyStatDto getEconomyStats() {
        com.mytripquest.domain.systemlog.dto.EconomyStatDto dto = new com.mytripquest.domain.systemlog.dto.EconomyStatDto();
        dto.setTotalPointsEarned(systemLogMapper.selectTotalPointsEarned());
        dto.setTotalPointsSpent(systemLogMapper.selectTotalPointsSpent());
        dto.setTopSellingItems(systemLogMapper.selectTopSellingItems());
        return dto;
    }

    @Transactional(readOnly = true)
    public com.mytripquest.domain.systemlog.dto.ContentStatDto getContentStats() {
        com.mytripquest.domain.systemlog.dto.ContentStatDto dto = new com.mytripquest.domain.systemlog.dto.ContentStatDto();

        // 1. Level Distribution
        java.util.List<Integer> allXp = userMapper.findAllUserXp();
        // Range: "1-10", "11-20" ... "91-99"
        Map<String, Integer> levelRangeCount = new HashMap<>();
        // Initialize ranges roughly
        for (int i = 0; i < 10; i++) {
            int start = i * 10 + 1;
            int end = (i + 1) * 10;
            if (end > 99)
                end = 99;
            String range = start + "-" + end;
            levelRangeCount.put(range, 0);
        }

        for (Integer xp : allXp) {
            if (xp == null)
                xp = 0;
            int level = com.mytripquest.domain.user.util.LevelUtil.calculateLevel(xp);
            int rangeIndex = (level - 1) / 10;
            // Cap at index 9 for levels 91-99 (or 100 if logic varies)
            if (rangeIndex > 9)
                rangeIndex = 9;

            int start = rangeIndex * 10 + 1;
            int end = (rangeIndex + 1) * 10;
            if (end > 99)
                end = 99;
            String range = start + "-" + end;

            levelRangeCount.put(range, levelRangeCount.getOrDefault(range, 0) + 1);
        }

        java.util.List<com.mytripquest.domain.systemlog.dto.ContentStatDto.UserLevelStat> levelStats = new java.util.ArrayList<>();
        java.util.List<String> sortedKeys = new java.util.ArrayList<>(levelRangeCount.keySet());
        sortedKeys.sort((s1, s2) -> {
            int start1 = Integer.parseInt(s1.split("-")[0]);
            int start2 = Integer.parseInt(s2.split("-")[0]);
            return Integer.compare(start1, start2);
        });

        for (String key : sortedKeys) {
            levelStats.add(new com.mytripquest.domain.systemlog.dto.ContentStatDto.UserLevelStat(key,
                    levelRangeCount.get(key)));
        }
        dto.setUserLevelStats(levelStats);

        // 2. Regional Stats
        dto.setRegionalStats(userQuestRepository.selectRegionalCompletionStats());

        return dto;
    }
}
