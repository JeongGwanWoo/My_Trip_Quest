package com.mytripquest.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CityProgressDto {
    private String cityName;
    private String areaCode;
    private int completedQuests;
    private int totalQuests;
}
