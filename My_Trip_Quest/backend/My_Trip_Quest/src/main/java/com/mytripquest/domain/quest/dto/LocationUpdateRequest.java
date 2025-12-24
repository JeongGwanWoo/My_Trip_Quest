package com.mytripquest.domain.quest.dto;

import lombok.Data;

@Data
public class LocationUpdateRequest {
    private Double latitude;
    private Double longitude;
    private Integer gpsVerifyRadius;
}
