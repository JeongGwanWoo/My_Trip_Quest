package com.mytripquest.domain.quest.service;

import com.mytripquest.domain.quest.dto.AreaQuestCountDto;
import com.mytripquest.domain.quest.dto.LocationWithQuestCountDto;
import com.mytripquest.domain.quest.entity.Quest;

import java.util.List;

public interface QuestService {
    List<AreaQuestCountDto> getAreaQuestCounts();
    List<LocationWithQuestCountDto> getLocationsByAreaCode(String areaCode);
    List<Quest> getQuestsByLocationId(Long locationId);
    void acceptQuest(long questId, long userId);
}
