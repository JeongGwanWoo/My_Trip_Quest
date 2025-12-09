package com.mytripquest.domain.quest.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestCompleteRequestDto {
    private Double latitude;
    private Double longitude;
}
