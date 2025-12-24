package com.mytripquest.domain.quest.service;

import com.mytripquest.domain.quest.dto.CompletedMissionResponse;
import com.mytripquest.domain.quest.dto.InProgressQuestDto;
import com.mytripquest.domain.quest.dto.QuestCompleteRequestDto;
import com.mytripquest.domain.quest.dto.UserAreaQuestStatusDto;
import com.mytripquest.domain.quest.dto.LocationWithQuestCountDto;
import com.mytripquest.domain.quest.dto.QuestLocationSliceDto;
import com.mytripquest.domain.quest.dto.QuestInfoWithStatusDto;
import com.mytripquest.domain.quest.entity.Quest;
import com.mytripquest.domain.ai.dto.LocationRadiusRequest;
import com.mytripquest.domain.ai.dto.RadiusEstimateResult;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface QuestService {
    List<UserAreaQuestStatusDto> getUserAreaQuestCounts(Long userId);

    QuestLocationSliceDto getLocationsByAreaCode(String areaCode, Long userId, String keyword, Pageable pageable);

    List<LocationWithQuestCountDto> getAllLocations(Long userId);

    List<QuestInfoWithStatusDto> getQuestsByLocationId(Long locationId, Long userId);

    void acceptQuest(long questId, long userId);

    void forfeitQuest(long questId, long userId);

    void completeArrivalQuest(long questId, long userId, QuestCompleteRequestDto request);

    void completePhotoQuest(long questId, long userId, MultipartFile imageFile, BigDecimal latitude,
            BigDecimal longitude) throws IOException;

    List<InProgressQuestDto> getInProgressQuests(Long userId);

    List<CompletedMissionResponse> getCompletedMissions(Long userId);

    int generateQuestsFromTourApi(List<java.util.Map<String, Object>> items, List<String> types, String areaCode);

    int estimateLocationRadius(String locationName, String address);

    List<RadiusEstimateResult> estimateLocationRadiusBatch(List<LocationRadiusRequest> locations);

    Map<String, Object> batchRecalculateRadius(List<Long> locationIds);

    void updateLocationRadius(Long locationId, int radius);

    void updateLocation(Long locationId, Double latitude, Double longitude, Integer gpsVerifyRadius);

    void createLocationWithQuests(com.mytripquest.domain.quest.dto.LocationCreateRequest request);

    void deleteQuest(Long questId);

    void createQuest(Quest quest);
}
