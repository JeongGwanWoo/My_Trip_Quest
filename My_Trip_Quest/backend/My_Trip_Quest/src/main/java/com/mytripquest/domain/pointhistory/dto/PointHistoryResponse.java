package com.mytripquest.domain.pointhistory.dto;

import com.mytripquest.domain.pointhistory.PointHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointHistoryResponse {
    private Long id;
    private String description;
    private Integer amount;
    private Integer balance;
    private LocalDateTime createdAt;

    public static PointHistoryResponse from(PointHistory pointHistory) {
        return PointHistoryResponse.builder()
                .id(pointHistory.getId())
                .description(pointHistory.getDescription())
                .amount(pointHistory.getAmount())
                .balance(pointHistory.getBalance())
                .createdAt(pointHistory.getCreatedAt())
                .build();
    }
}
