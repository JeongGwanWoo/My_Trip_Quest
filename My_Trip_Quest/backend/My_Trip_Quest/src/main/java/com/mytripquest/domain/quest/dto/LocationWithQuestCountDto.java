package com.mytripquest.domain.quest.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 특정 지역에 속한, 퀘스트가 있는 관광지 정보를 담는 DTO
 */
@Data
public class LocationWithQuestCountDto {
    private long locationId;
    private String title;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer gpsVerifyRadius;
    private int questCount;
}
