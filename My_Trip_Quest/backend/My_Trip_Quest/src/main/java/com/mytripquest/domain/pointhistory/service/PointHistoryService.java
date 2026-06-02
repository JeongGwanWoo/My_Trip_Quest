package com.mytripquest.domain.pointhistory.service;

import com.mytripquest.domain.pointhistory.dto.PointHistoryResponse;
import com.mytripquest.domain.pointhistory.repository.PointHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointHistoryService {

    private final PointHistoryMapper pointHistoryMapper;

    public List<PointHistoryResponse> getPointHistory(Long userId) {
        return pointHistoryMapper.findByUserId(userId).stream()
                .map(PointHistoryResponse::from)
                .collect(Collectors.toList());
    }
}
