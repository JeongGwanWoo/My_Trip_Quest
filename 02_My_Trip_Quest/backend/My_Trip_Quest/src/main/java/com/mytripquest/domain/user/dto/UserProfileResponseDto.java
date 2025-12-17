package com.mytripquest.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class UserProfileResponseDto {
    private String email;
    private String nickname;
    private LocalDate joinedAt;
    private int points;
    private LevelProgressDto levelProgress;

    private long completedMissions;
    private long ongoingMissions;
    private long totalMissions;
    private int rank;
    private List<CityProgressDto> cityProgress;
}
