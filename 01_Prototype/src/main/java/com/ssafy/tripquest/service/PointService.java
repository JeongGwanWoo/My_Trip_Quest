package com.ssafy.tripquest.service;

import com.ssafy.tripquest.domain.PointLog;
import com.ssafy.tripquest.mapper.PointLogMapper;
import com.ssafy.tripquest.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserMapper userMapper;
    private final PointLogMapper pointLogMapper;

    @Transactional
    public int earnPoints(Long userId, int amount, PointLog.SourceType sourceType, Long sourceId, String description) {
        userMapper.updateUserPoint(userId, amount);

        int newBalance = userMapper.findPointBalanceByUserId(userId);

        PointLog log = PointLog.builder()
                .userId(userId)
                .transactionType(PointLog.TransactionType.EARN)
                .amount(amount)
                .balanceAfter(newBalance)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .description(description)
                .build();
        pointLogMapper.save(log);

        return newBalance;
    }
}
