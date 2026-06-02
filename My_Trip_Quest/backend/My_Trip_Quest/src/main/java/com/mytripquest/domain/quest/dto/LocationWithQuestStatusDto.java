package com.mytripquest.domain.quest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class LocationWithQuestStatusDto {
    private long locationId;
    private String title;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer gpsVerifyRadius;
    private int questCount;
    private String status; // IN_PROGRESS, COMPLETED, null
}
