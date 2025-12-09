package com.mytripquest.domain.quest.service;

import com.mytripquest.domain.quest.dto.InProgressQuestDto;
import com.mytripquest.domain.quest.dto.QuestCompleteRequestDto;
import com.mytripquest.domain.quest.dto.UserAreaQuestStatusDto;
import com.mytripquest.domain.quest.dto.LocationWithQuestCountDto;
import com.mytripquest.domain.quest.entity.Quest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface QuestService {
    List<UserAreaQuestStatusDto> getUserAreaQuestCounts(Long userId);
    List<LocationWithQuestCountDto> getLocationsByAreaCode(String areaCode);
    List<Quest> getQuestsByLocationId(Long locationId);
    void acceptQuest(long questId, long userId);
    void completeArrivalQuest(long questId, long userId, QuestCompleteRequestDto request);
    void completePhotoQuest(long questId, long userId, MultipartFile imageFile) throws IOException;
    List<InProgressQuestDto> getInProgressQuests(Long userId);
}

