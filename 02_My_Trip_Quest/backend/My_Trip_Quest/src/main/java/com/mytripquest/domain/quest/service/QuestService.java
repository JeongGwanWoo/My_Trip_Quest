package com.mytripquest.domain.quest.service;

import com.mytripquest.domain.quest.dto.InProgressQuestDto;
import com.mytripquest.domain.quest.dto.QuestCompleteRequestDto;
import com.mytripquest.domain.quest.dto.UserAreaQuestStatusDto;
import com.mytripquest.domain.quest.dto.LocationWithQuestCountDto;
import com.mytripquest.domain.quest.entity.Quest;

import java.util.List;

public interface QuestService {
    List<UserAreaQuestStatusDto> getUserAreaQuestCounts(Long userId);
    List<LocationWithQuestCountDto> getLocationsByAreaCode(String areaCode);
    List<Quest> getQuestsByLocationId(Long locationId);
    void acceptQuest(long questId, long userId);
    void completeQuest(long questId, long userId, QuestCompleteRequestDto request);
    List<InProgressQuestDto> getInProgressQuests(Long userId);
}

