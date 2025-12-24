package com.mytripquest.domain.user.dto;

import com.mytripquest.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminUserResponseDto {
    private Long userId;
    private String email;
    private String nickname;
    private String provider;
    private User.Role role;
    private Integer totalXp;
    private Integer points;
    private LocalDateTime createdAt;

    public static AdminUserResponseDto from(User user) {
        return AdminUserResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .role(user.getRole())
                .totalXp(user.getTotalXp())
                .points(user.getPoints())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
