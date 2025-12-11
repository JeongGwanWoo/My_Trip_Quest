package com.mytripquest.domain.quest.service;

import com.mytripquest.domain.quest.dto.QuestCompleteRequestDto;
import com.mytripquest.domain.ai.service.AIVisionService;
import com.mytripquest.domain.quest.dto.InProgressQuestDto;
import com.mytripquest.domain.quest.dto.LocationWithQuestCountDto;
import com.mytripquest.domain.quest.dto.QuestInfoWithStatusDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.lang.GeoLocation;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 퀘스트 관련 비즈니스 로직을 처리하는 서비스 구현체
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
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
    public List<QuestInfoWithStatusDto> getQuestsByLocationId(Long locationId, Long userId) {
        // 1. 특정 관광지의 모든 퀘스트 목록 조회
        List<Quest> quests = questRepository.findQuestsByLocationId(locationId);
        if (quests.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 퀘스트 ID 리스트 추출
        List<Long> questIds = quests.stream().map(Quest::getQuestId).collect(Collectors.toList());

        // 3. 사용자의 퀘스트 진행 상태 조회
        List<UserQuest> userQuests = userQuestRepository.findByUserIdAndQuestIds(userId, questIds);
        Map<Long, QuestStatus> questStatusMap = userQuests.stream()
                .collect(Collectors.toMap(UserQuest::getQuestId, UserQuest::getStatus));

        // 4. 퀘스트 정보와 상태를 합쳐 DTO 리스트 생성
        return quests.stream().map(quest -> {
            QuestStatus status = questStatusMap.get(quest.getQuestId());
            return QuestInfoWithStatusDto.builder()
                    .questId(quest.getQuestId())
                    .locationId(quest.getLocationId())
                    .questTypeId(quest.getQuestTypeId())
                    .previousQuestId(quest.getPreviousQuestId())
                    .difficulty(quest.getDifficulty())
                    .title(quest.getTitle())
                    .description(quest.getDescription())
                    .rewardXp(quest.getRewardXp())
                    .rewardPoints(quest.getRewardPoints())
                    .requireGpsVerify(quest.isRequireGpsVerify())
                    .isActive(quest.isActive())
                    .status(status) // null일 경우 아직 시작하지 않은 퀘스트
                    .build();
        }).collect(Collectors.toList());
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

    @Override
    public void forfeitQuest(long questId, long userId) {
        // 1. 사용자 퀘스트 정보 조회
        UserQuest userQuest = userQuestRepository.findByUserIdAndQuestId(userId, questId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUEST_NOT_ACCEPTED)); // 수락한 적 없는 퀘스트

        // 2. 퀘스트 상태 확인
        if (userQuest.getStatus() != QuestStatus.ACCEPTED) {
            throw new BusinessException(ErrorCode.QUEST_NOT_IN_FORFEITABLE_STATE); // 이미 진행 중이거나 완료된 퀘스트
        }

        // 3. 퀘스트 포기 처리 (레코드 삭제)
        userQuestRepository.delete(userQuest);
    }

    /**
     * 사용자가 퀘스트를 완료 처리합니다.
     * @param questId 완료할 퀘스트 ID
     * @param userId 완료하는 사용자 ID
     */
    @Override
    public void completeArrivalQuest(long questId, long userId, QuestCompleteRequestDto request) {
        completeQuestInternal(questId, userId, request, null, null, null);
    }

    @Override
    public void completePhotoQuest(long questId, long userId, MultipartFile imageFile, BigDecimal latitude, BigDecimal longitude) throws IOException {
        completeQuestInternal(questId, userId, null, imageFile, latitude, longitude);
    }

    private void completeQuestInternal(long questId, long userId, QuestCompleteRequestDto arrivalRequest, MultipartFile photoFile, BigDecimal currentLat, BigDecimal currentLon) {
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
            case 1: // 도착 미션
                verifyArrivalQuest(quest, arrivalRequest);
                break;
            case 2: // 사진 미션
                performPhotoVerification(quest, userId, photoFile, currentLat, currentLon);
                break;
            default:
                // 지원하지 않는 퀘스트 타입에 대한 예외 처리나 로깅
                break;
        }

        // 4. 퀘스트 상태를 COMPLETED로 업데이트
        userQuest.setStatus(QuestStatus.COMPLETED);
        userQuest.setCompletedAt(java.time.LocalDateTime.now());
        userQuestRepository.update(userQuest);

        // 5. 퀘스트 완료 보상 지급
        grantQuestRewards(userId, quest);
    }

    private void performPhotoVerification(Quest quest, Long userId, MultipartFile imageFile, BigDecimal currentLat, BigDecimal currentLon) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PHOTO_PROOF);
        }

        LocationWithQuestCountDto location = questRepository.findLocationById(quest.getLocationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_FOUND));

        BigDecimal verificationLat;
        BigDecimal verificationLon;

        try {
            byte[] fileBytes = imageFile.getBytes();

            if (currentLat != null && currentLon != null) {
                log.info("--- 제공된 현재 위치로 사진 미션 인증 ---");
                verificationLat = currentLat;
                verificationLon = currentLon;
            } else {
                log.info("--- 사진 메타데이터로 사진 미션 인증 ---");
                Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(fileBytes));
                GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

                if (gpsDirectory == null || !gpsDirectory.containsTag(GpsDirectory.TAG_LATITUDE) || !gpsDirectory.containsTag(GpsDirectory.TAG_LONGITUDE)) {
                    throw new BusinessException(ErrorCode.PHOTO_METADATA_MISSING);
                }
                
                GeoLocation photoLocation = gpsDirectory.getGeoLocation();
                verificationLat = BigDecimal.valueOf(photoLocation.getLatitude());
                verificationLon = BigDecimal.valueOf(photoLocation.getLongitude());

                Date photoTimestamp;
                ExifSubIFDDirectory exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                photoTimestamp = (exifSubIFDDirectory != null) ? exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL) : null;

                if (photoTimestamp == null) {
                    throw new BusinessException(ErrorCode.PHOTO_METADATA_MISSING);
                }
                log.info("사진 촬영 시간: {}", photoTimestamp);

                Quest arrivalQuest = questRepository.findFirstByLocationIdAndQuestTypeIdOrderByQuestIdAsc(location.getLocationId(), 1)
                        .orElseThrow(() -> new BusinessException(ErrorCode.ARRIVAL_QUEST_NOT_FOUND));

                UserQuest arrivalUserQuest = userQuestRepository.findByUserIdAndQuestId(userId, arrivalQuest.getQuestId())
                        .filter(uq -> uq.getStatus() == QuestStatus.COMPLETED)
                        .orElseThrow(() -> new BusinessException(ErrorCode.ARRIVAL_QUEST_NOT_COMPLETED));

                Date arrivalMissionCompletionTime = Date.from(arrivalUserQuest.getCompletedAt().atZone(ZoneId.systemDefault()).toInstant());
                log.info("도착 미션 완료 시간: {}", arrivalMissionCompletionTime);

                long timeDifferenceMillis = photoTimestamp.getTime() - arrivalMissionCompletionTime.getTime();
                if (timeDifferenceMillis < 0) {
                    throw new BusinessException(ErrorCode.PHOTO_TIME_BEFORE_ARRIVAL_MISSION);
                } else if (timeDifferenceMillis > TimeUnit.HOURS.toMillis(24)) {
                    throw new BusinessException(ErrorCode.PHOTO_TIME_EXCEEDS_24_HOURS);
                }
            }
            
            double distance = calculateDistance(verificationLat, verificationLon, location.getLatitude(), location.getLongitude());
            double maxDistance = location.getGpsVerifyRadius() != null ? location.getGpsVerifyRadius() : 50.0;

            if (distance > maxDistance) {
                log.warn("사진 미션 실패 (사용자 {}): 거리 {}m가 요구 반경 {}m보다 큽니다.", userId, String.format("%.2f", distance), maxDistance);
                throw new BusinessException(ErrorCode.DISTANCE_TOO_FAR);
            }

            boolean isLandmarkPhoto = aiVisionService.isPhotoOfLandmark(fileBytes, location.getTitle());
            if (!isLandmarkPhoto) {
                log.warn("사진 미션 실패 (사용자 {}): AI가 사진 내용이 랜드마크 '{}'와 일치하지 않는다고 판단했습니다.", userId, location.getTitle());
                throw new BusinessException(ErrorCode.INVALID_PHOTO_PROOF);
            }

        } catch (ImageProcessingException | IOException e) {
            log.error("사진 파일을 처리하는 중 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
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
            throw new BusinessException(ErrorCode.GPS_COORDINATES_REQUIRED);
        }

        LocationWithQuestCountDto location = questRepository.findLocationById(quest.getLocationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_FOUND));

        if (location.getLatitude() == null || location.getLongitude() == null) {
            throw new BusinessException(ErrorCode.GPS_COORDINATES_REQUIRED);
        }

        double distance = calculateDistance(BigDecimal.valueOf(request.getLatitude()), BigDecimal.valueOf(request.getLongitude()),
                location.getLatitude(), location.getLongitude());

        double maxDistance = location.getGpsVerifyRadius() != null ? location.getGpsVerifyRadius() : 50.0;
        if (distance > maxDistance) {
            throw new BusinessException(ErrorCode.DISTANCE_TOO_FAR);
        }
    }

    /**
     * 두 지점 간의 거리를 미터 단위로 계산합니다 (Haversine formula).
     */
    private double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            throw new BusinessException(ErrorCode.GPS_COORDINATES_REQUIRED);
        }

        final int R = 6371; // 지구 반지름 (킬로미터)

        double dLat1 = Math.toRadians(lat1.doubleValue());
        double dLon1 = Math.toRadians(lon1.doubleValue());
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
