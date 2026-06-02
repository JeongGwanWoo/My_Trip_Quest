package com.mytripquest.domain.quest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestLocationSliceDto {
    private List<LocationWithQuestStatusDto> content;
    private boolean last;
}
