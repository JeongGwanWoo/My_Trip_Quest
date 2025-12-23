package com.mytripquest.domain.quest.dto;

import lombok.Data;

@Data
public class QuestStatDto {
    private Long questId;
    private String questTitle;
    private long completionCount;
    private long acceptanceCount;
    private double completionRate;
}
