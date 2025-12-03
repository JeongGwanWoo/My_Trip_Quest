package com.mytripquest.domain.user.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avatar {

    private Long userId;
    private String characterStyle;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
