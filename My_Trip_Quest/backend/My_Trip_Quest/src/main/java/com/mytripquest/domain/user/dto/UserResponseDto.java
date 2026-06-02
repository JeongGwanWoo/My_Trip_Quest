package com.mytripquest.domain.user.dto;

import com.mytripquest.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponseDto {

    @Getter
    @Builder
    public static class ProfileResponseDto {
        private String email;
        private String nickname;
        private int totalXp;
        private int points;

        public static ProfileResponseDto from(User user) {
            return ProfileResponseDto.builder()
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .totalXp(user.getTotalXp())
                    .points(user.getPoints())
                    .build();
        }
    }
}
