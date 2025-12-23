package com.mytripquest.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestCompleteRequestDto {
    private Double latitude;
    private Double longitude;
}
