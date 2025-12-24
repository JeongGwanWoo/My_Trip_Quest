package com.mytripquest.domain.pointhistory;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointHistory {
    private Long id;
    private Long userId;
    private String description;
    private Integer amount;
    private Integer balance;
    private LocalDateTime createdAt;
}
