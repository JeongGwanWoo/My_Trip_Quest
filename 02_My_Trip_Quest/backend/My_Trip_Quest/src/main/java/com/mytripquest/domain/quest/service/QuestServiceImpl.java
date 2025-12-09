package com.mytripquest.domain.quest.service;

import com.mytripquest.domain.quest.dto.QuestCompleteRequestDto;
import com.mytripquest.domain.ai.service.AIVisionService;
import com.mytripquest.domain.quest.dto.InProgressQuestDto;
import com.mytripquest.domain.quest.dto.LocationWithQuestCountDto;
import com.mytripquest.domain.quest.dto.UserAreaQuestStatusDto;
import com.mytripquest.domain.quest.entity.Quest;
import com.mytripquest.domain.quest.entity.QuestStatus;
import com.mytripquest.domain.quest.entity.UserQuest;
import com.mytripquest.domain.quest.repository.QuestRepository;
import com.mytripquest.domain.quest.repository.UserQuestRepository;
import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 퀘스트 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Service
@Transactional
@RequiredArgsConstructor
public class QuestServiceImpl implements QuestService {

    private final QuestRepository questRepository;
    private final UserQuestRepository userQuestRepository;
    private final UserMapper userMapper;
    private final AIVisionService aiVisionService;
    // MVP 단계에서는 지역 정보를 하드코딩하여 사용
    private static final Map<String, String> AREA_CODES;
    private static final Map<String, String> CODE_TO_NAME;


    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("서울특별시", "1");
        aMap.put("광주광역시", "5");
        AREA_CODES = Collections.unmodifiableMap(aMap);
        CODE_TO_NAME = Collections.unmodifiableMap(aMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
    }

    /**
     * 각 지역별 퀘스트 개수와 사용자의 완료 현황을 조회합니다.
     * @param userId 현재 사용자의 ID
     * @return 각 지역의 이름, 총 퀘스트 개수, 완료한 퀘스트 개수를 담은 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserAreaQuestStatusDto> getUserAreaQuestCounts(Long userId) {
        List<UserAreaQuestStatusDto> areaQuestStatus = new ArrayList<>();
        for (Map.Entry<String, String> entry : AREA_CODES.entrySet()) {
            String areaName = entry.getKey();
            String areaCode = entry.getValue();

            // 1. 해당 지역의 전체 관광지 수 계산
            int totalLocations = questRepository.countTotalLocationsByArea(areaCode);

            // 2. 해당 지역에서 사용자가 완료하지 않은 관광지 수 계산
            int incompleteLocations = userQuestRepository.countIncompleteLocationsByArea(userId, areaCode);

            // 3. DTO 생성
            areaQuestStatus.add(UserAreaQuestStatusDto.builder()
                    .areaName(areaName)
                    .areaCode(areaCode)
                    .incompleteLocationCount(incompleteLocations)
                    .totalLocationCount(totalLocations)
                    .build());
        }
        return areaQuestStatus;
    }

    /**
     * 지역 코드를 받아 해당 지역의 퀘스트가 있는 관광지 목록을 조회합니다.
     * @param areaCode (1, 5 등)
     * @return 해당 지역의 관광지 정보와 퀘스트 개수를 담은 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<LocationWithQuestCountDto> getLocationsByAreaCode(String areaCode) {
        if (!CODE_TO_NAME.containsKey(areaCode)) {
            return Collections.emptyList();
        }
        return questRepository.findLocationsByAreaCode(areaCode);
    }

    /**
     * 특정 관광지 ID를 기준으로 해당 관광지에 속한 퀘스트 목록을 조회합니다.
     * @param locationId
     * @return 퀘스트 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<Quest> getQuestsByLocationId(Long locationId) {
        return questRepository.findQuestsByLocationId(locationId);
    }

    /**
     * 사용자가 퀘스트를 수락하는 로직을 처리합니다.
     * @param questId 수락할 퀘스트 ID
     * @param userId 수락하는 사용자 ID
     */
    @Override
    public void acceptQuest(long questId, long userId) {
        // 1. 퀘스트 정보 조회
        Quest quest = questRepository.findQuestById(questId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUEST_NOT_FOUND));

        // 2. 이미 수락한 퀘스트인지 확인
        userQuestRepository.findByUserIdAndQuestId(userId, questId).ifPresent(userQuest -> {
            throw new BusinessException(ErrorCode.QUEST_ALREADY_ACCEPTED);
        });

        // 3. 선행 퀘스트 조건 확인
        if (quest.getPreviousQuestId() != null && quest.getPreviousQuestId() > 0) {
            userQuestRepository.findCompletedByUserIdAndQuestId(userId, quest.getPreviousQuestId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PREVIOUS_QUEST_NOT_COMPLETED));
        }

        // 4. 퀘스트 수락 처리
        UserQuest newUserQuest = UserQuest.builder()
                .userId(userId)
                .questId(questId)
                .status(QuestStatus.ACCEPTED)
                .build();
        userQuestRepository.save(newUserQuest);
    }

    /**
     * 사용자가 퀘스트를 완료 처리합니다.
     * @param questId 완료할 퀘스트 ID
     * @param userId 완료하는 사용자 ID
     */
    @Override
    public void completeArrivalQuest(long questId, long userId, QuestCompleteRequestDto request) {
        completeQuestInternal(questId, userId, request, null);
    }

    @Override
    public void completePhotoQuest(long questId, long userId, MultipartFile imageFile) throws IOException {
        completeQuestInternal(questId, userId, null, imageFile);
    }

    private void completeQuestInternal(long questId, long userId, QuestCompleteRequestDto arrivalRequest, MultipartFile photoFile) {
        // 1. 퀘스트 및 사용자-퀘스트 정보 조회
        Quest quest = questRepository.findQuestById(questId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUEST_NOT_FOUND));
        UserQuest userQuest = userQuestRepository.findByUserIdAndQuestId(userId, questId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUEST_NOT_ACCEPTED));

        // 2. 퀘스트 상태 확인 (중복 완료 방지)
        if (userQuest.getStatus() == QuestStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.QUEST_ALREADY_COMPLETED);
        }
        if (userQuest.getStatus() != QuestStatus.ACCEPTED) {
            throw new BusinessException(ErrorCode.QUEST_NOT_ACCEPTED);
        }

        // 3. 퀘스트 타입별 완료 조건 검증
        switch (quest.getQuestTypeId()) {
            case 1:
                verifyArrivalQuest(quest, arrivalRequest);
                break;
            case 2:
                try {
                    verifyPhotoQuest(quest, photoFile);
                } catch (IOException e) {
                    throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
                break;
            default:
                // 지원하지 않는 퀘스트 타입에 대한 예외 처리나 로깅
                break;
        }

        // 4. 퀘스트 상태를 COMPLETED로 업데이트
        userQuest.setStatus(QuestStatus.COMPLETED);
        userQuestRepository.update(userQuest);

        // 5. 퀘스트 완료 보상 지급
        grantQuestRewards(userId, quest);
    }

    private void verifyPhotoQuest(Quest quest, MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PHOTO_PROOF);
        }

        LocationWithQuestCountDto location = questRepository.findLocationById(quest.getLocationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_FOUND));

        boolean isVerified = aiVisionService.isPhotoOfLandmark(imageFile.getBytes(), location.getTitle());

        if (!isVerified) {
            throw new BusinessException(ErrorCode.INVALID_PHOTO_PROOF);
        }
    }

    /**
     * 사용자에게 퀘스트 완료 보상(XP, Point)을 지급합니다.
     * @param userId 보상을 받을 사용자 ID
     * @param quest 완료된 퀘스트 정보
     */
    private void grantQuestRewards(long userId, Quest quest) {
        if (quest.getRewardXp() > 0 || quest.getRewardPoints() > 0) {
            User user = userMapper.findById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            User updatedUser = User.builder()
                    .userId(user.getUserId())
                    .totalXp(user.getTotalXp() + quest.getRewardXp())
                    .points(user.getPoints() + quest.getRewardPoints())
                    .build();

            userMapper.updateUser(updatedUser);
        }
    }

    /**
     * 도착 퀘스트의 완료 조건을 검증합니다 (GPS 좌표 기반).
     */
    private void verifyArrivalQuest(Quest quest, QuestCompleteRequestDto request) {
        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new BusinessException(ErrorCode.GPS_COORDINATES_REQUIRED); // 또는 GPS_REQUIRED 등
        }

        // 퀘스트에 연결된 장소 정보 조회
        LocationWithQuestCountDto location = questRepository.findLocationById(quest.getLocationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_FOUND));

        if (location.getLatitude() == null || location.getLongitude() == null) {
            throw new BusinessException(ErrorCode.GPS_COORDINATES_REQUIRED); // 장소에 좌표가 없는 경우
        }

        // 거리 계산 (미터 단위)
        double distance = calculateDistance(request.getLatitude(), request.getLongitude(),
                location.getLatitude(), location.getLongitude());

        // 예: 50미터 이내에 있어야 통과
        double MAX_DISTANCE_METERS = 50.0;
        if (distance > MAX_DISTANCE_METERS) {
            throw new BusinessException(ErrorCode.DISTANCE_TOO_FAR);
        }
    }

    /**
     * 두 지점 간의 거리를 미터 단위로 계산합니다 (Haversine formula).
     */
    private double calculateDistance(Double lat1, Double lon1, BigDecimal lat2, BigDecimal lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            throw new BusinessException(ErrorCode.GPS_COORDINATES_REQUIRED);
        }

        final int R = 6371; // 지구 반지름 (킬로미터)

        double dLat1 = Math.toRadians(lat1);
        double dLon1 = Math.toRadians(lon1);
        double dLat2 = Math.toRadians(lat2.doubleValue());
        double dLon2 = Math.toRadians(lon2.doubleValue());

        double latDistance = dLat2 - dLat1;
        double lonDistance = dLon2 - dLon1;
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(dLat1) * Math.cos(dLat2)
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000; // 미터 단위로 변환
    }

    /**
     * 현재 사용자가 진행 중인(ACCEPTED) 퀘스트 목록을 조회합니다.
     * @param userId 현재 사용자 ID
     * @return 진행 중인 퀘스트 정보 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<InProgressQuestDto> getInProgressQuests(Long userId) {
        return userQuestRepository.findUserQuestsByStatus(userId, QuestStatus.ACCEPTED);
    }
}
