package com.mytripquest.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class UserProfileResponseDto {
    private LocalDate joinedAt;
    private long completedMissions;
    private long totalMissions;
    private int rank;
    private List<CityProgressDto> cityProgress;
}
