package com.mytripquest.domain.user.entity;

import com.mytripquest.domain.user.util.LevelUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long userId;
    private String email;
    private String passwordHash;
    private String nickname;
    private Role role;
    private Integer totalXp;
    private Integer points;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Role {
        USER, ADMIN
    }

    public int getLevel() {
        if (this.totalXp == null) {
            return 1;
        }
        return LevelUtil.calculateLevel(this.totalXp);
    }
}
