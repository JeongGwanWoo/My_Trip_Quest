package com.mytripquest.domain.avatar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnequipRequest {
    private String slot; // 예: "HAIR", "TOP"
}