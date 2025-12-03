package com.mytripquest.domain.item.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserItem {

    private Long userId;
    private Long itemId;
    private int quantity;
    private boolean isEquipped;
    private LocalDateTime acquiredAt;
    private LocalDateTime updatedAt;
}
