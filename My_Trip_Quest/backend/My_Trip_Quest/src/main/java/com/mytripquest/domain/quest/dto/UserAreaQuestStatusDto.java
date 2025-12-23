package com.mytripquest.domain.quest.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserAreaQuestStatusDto {
    private final String areaName;
    private final String areaCode;
    private final int incompleteLocationCount;
    private final int totalLocationCount;

    @Builder
    public UserAreaQuestStatusDto(String areaName, String areaCode, int incompleteLocationCount, int totalLocationCount) {
        this.areaName = areaName;
        this.areaCode = areaCode;
        this.incompleteLocationCount = incompleteLocationCount;
        this.totalLocationCount = totalLocationCount;
    }
}
