package com.mytripquest.domain.quest.service;

import com.mytripquest.domain.quest.dto.AreaQuestCountDto;
import com.mytripquest.domain.quest.entity.Quest;

import java.util.List;

public interface QuestService {
    List<AreaQuestCountDto> getAreaQuestCounts();
    List<Quest> getQuestsByAreaCode(String areaCode);
}
