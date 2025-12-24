package com.mytripquest.domain.quest.service;

import com.mytripquest.domain.activitylog.service.ActivityLogService;
import com.mytripquest.domain.quest.dto.QuestCompleteRequestDto;
import com.mytripquest.domain.ai.service.AIService;
import com.mytripquest.domain.ai.dto.LocationRadiusRequest;
import com.mytripquest.domain.ai.dto.RadiusEstimateResult;
import com.mytripquest.domain.quest.dto.InProgressQuestDto;
import com.mytripquest.domain.quest.dto.LocationWithQuestCountDto;
import com.mytripquest.domain.quest.dto.LocationWithQuestStatusDto;
import com.mytripquest.domain.quest.dto.QuestLocationSliceDto;
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
import com.mytripquest.domain.quest.entity.Difficulty;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class QuestServiceImpl implements QuestService {

    private final QuestRepository questRepository;
    private final UserQuestRepository userQuestRepository;
    private final UserMapper userMapper;
    private final AIService aiService;
    private final ActivityLogService activityLogService;
    private static final Map<String, String> AREA_CODES;
    private static final Map<String, String> CODE_TO_NAME;

    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("서울특별시", "1");
        aMap.put("인천광역시", "2");
        aMap.put("대전광역시", "3");
        aMap.put("대구광역시", "4");
        aMap.put("광주광역시", "5");
        aMap.put("부산광역시", "6");
        aMap.put("울산광역시", "7");
        aMap.put("세종특별자치시", "8");
        aMap.put("경기도", "31");
        aMap.put("강원특별자치도", "32");
        aMap.put("충청북도", "33");
        aMap.put("충청남도", "34");
        aMap.put("경상북도", "35");
        aMap.put("경상남도", "36");
        aMap.put("전북특별자치도", "37");
        aMap.put("전라남도", "38");
        aMap.put("제주특별자치도", "39");
        AREA_CODES = Collections.unmodifiableMap(aMap);
        CODE_TO_NAME = Collections.unmodifiableMap(
                aMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAreaQuestStatusDto> getUserAreaQuestCounts(Long userId) {
        List<UserAreaQuestStatusDto> areaQuestStatus = new ArrayList<>();
        // 순서 보장을 위해 정렬된 키셋 사용 (가나다 순) 또는 코드 순?
        // 여기서는 Map 순서에 의존하지 않고 리스트에 담은 뒤 필요시 정렬하거나, 클라이언트가 처리.
        // HashMap이라 순서 보장 안됨. -> TreeMap 사용이나 Stream sorted 고려 가능.
        // 일단 기존 로직 유지하되 필터링 추가.

        for (Map.Entry<String, String> entry : AREA_CODES.entrySet()) {
            String areaName = entry.getKey();
            String areaCode = entry.getValue();

            int totalLocations = questRepository.countTotalLocationsByArea(areaCode);

            // 퀘스트(장소)가 하나도 없는 지역은 제외
            if (totalLocations == 0) {
                continue;
            }

            int incompleteLocations;
            if (userId == null) {
                incompleteLocations = totalLocations;
            } else {
                incompleteLocations = userQuestRepository.countIncompleteLocationsByArea(userId, areaCode);
            }

            areaQuestStatus.add(UserAreaQuestStatusDto.builder()
                    .areaName(areaName)
                    .areaCode(areaCode)
                    .incompleteLocationCount(incompleteLocations)
                    .totalLocationCount(totalLocations)
                    .build());
        }

        // areaCode 기준으로 오름차순 정렬 (1, 2, ... 31, ...)
        areaQuestStatus.sort((o1, o2) -> {
            try {
                return Integer.compare(Integer.parseInt(o1.getAreaCode()), Integer.parseInt(o2.getAreaCode()));
            } catch (NumberFormatException e) {
                return o1.getAreaCode().compareTo(o2.getAreaCode());
            }
        });

        return areaQuestStatus;
    }

    @Override
    @Transactional(readOnly = true)
    public QuestLocationSliceDto getLocationsByAreaCode(String areaCode, Long userId, String keyword,
            Pageable pageable) {
        if (!"ALL".equals(areaCode) && !CODE_TO_NAME.containsKey(areaCode)) {
            return new QuestLocationSliceDto(Collections.emptyList(), true);
        }

        Pageable queryPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize() + 1);
        List<LocationWithQuestCountDto> locations = questRepository.findLocationsByAreaCode(areaCode, keyword,
                queryPageable);

        boolean hasNext = locations.size() > pageable.getPageSize();
        List<LocationWithQuestCountDto> content = hasNext ? locations.subList(0, pageable.getPageSize()) : locations;

        if (content.isEmpty()) {
            return new QuestLocationSliceDto(Collections.emptyList(), !hasNext);
        }

        if (userId == null) {
            List<LocationWithQuestStatusDto> dtoList = content.stream().map(loc -> {
                LocationWithQuestStatusDto dto = new LocationWithQuestStatusDto();
                dto.setLocationId(loc.getLocationId());
                dto.setTitle(loc.getTitle());
                dto.setLatitude(loc.getLatitude());
                dto.setLongitude(loc.getLongitude());
                dto.setGpsVerifyRadius(loc.getGpsVerifyRadius());
                dto.setQuestCount(loc.getQuestCount());
                dto.setStatus(null);
                return dto;
            }).collect(Collectors.toList());
            return new QuestLocationSliceDto(dtoList, !hasNext);
        }

        List<Long> locationIds = content.stream().map(LocationWithQuestCountDto::getLocationId)
                .collect(Collectors.toList());
        List<Quest> questsInArea = questRepository.findQuestsByAreaCode(areaCode);
        List<Quest> questsForContent = questsInArea.stream()
                .filter(q -> locationIds.contains(q.getLocationId()))
                .collect(Collectors.toList());

        if (questsForContent.isEmpty()) {
            List<LocationWithQuestStatusDto> dtoList = content.stream().map(loc -> {
                LocationWithQuestStatusDto dto = new LocationWithQuestStatusDto();
                dto.setLocationId(loc.getLocationId());
                dto.setTitle(loc.getTitle());
                dto.setLatitude(loc.getLatitude());
                dto.setLongitude(loc.getLongitude());
                dto.setGpsVerifyRadius(loc.getGpsVerifyRadius());
                dto.setQuestCount(loc.getQuestCount());
                dto.setStatus(null);
                return dto;
            }).collect(Collectors.toList());
            return new QuestLocationSliceDto(dtoList, !hasNext);
        }

        List<Long> questIds = questsForContent.stream().map(Quest::getQuestId).collect(Collectors.toList());
        List<UserQuest> userQuests = userQuestRepository.findByUserIdAndQuestIds(userId, questIds);
        Map<Long, List<QuestStatus>> locationToStatusMap = new HashMap<>();
        Map<Long, Quest> questIdToQuestMap = questsForContent.stream()
                .collect(Collectors.toMap(Quest::getQuestId, q -> q));

        for (UserQuest userQuest : userQuests) {
            Quest quest = questIdToQuestMap.get(userQuest.getQuestId());
            if (quest != null) {
                locationToStatusMap
                        .computeIfAbsent(quest.getLocationId(), k -> new ArrayList<>())
                        .add(userQuest.getStatus());
            }
        }

        List<LocationWithQuestStatusDto> dtoList = content.stream().map(locationDto -> {
            LocationWithQuestStatusDto statusDto = new LocationWithQuestStatusDto();
            statusDto.setLocationId(locationDto.getLocationId());
            statusDto.setTitle(locationDto.getTitle());
            statusDto.setLatitude(locationDto.getLatitude());
            statusDto.setLongitude(locationDto.getLongitude());
            statusDto.setGpsVerifyRadius(locationDto.getGpsVerifyRadius());
            statusDto.setQuestCount(locationDto.getQuestCount());

            List<QuestStatus> statuses = locationToStatusMap.get(locationDto.getLocationId());
            if (statuses != null) {
                if (statuses.contains(QuestStatus.ACCEPTED) || statuses.contains(QuestStatus.IN_PROGRESS)) {
                    statusDto.setStatus("IN_PROGRESS");
                } else if (statuses.stream().allMatch(s -> s == QuestStatus.COMPLETED)) {
                    statusDto.setStatus("COMPLETED");
                }
            }
            return statusDto;
        }).collect(Collectors.toList());

        return new QuestLocationSliceDto(dtoList, !hasNext);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestInfoWithStatusDto> getQuestsByLocationId(Long locationId, Long userId) {
        List<Quest> quests = questRepository.findQuestsByLocationId(locationId);
        if (quests.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, QuestStatus> questStatusMap = new HashMap<>();
        if (userId != null) {
            List<Long> questIds = quests.stream().map(Quest::getQuestId).collect(Collectors.toList());
            List<UserQuest> userQuests = userQuestRepository.findByUserIdAndQuestIds(userId, questIds);
            questStatusMap = userQuests.stream()
                    .collect(Collectors.toMap(UserQuest::getQuestId, UserQuest::getStatus));
        }

        final Map<Long, QuestStatus> finalQuestStatusMap = questStatusMap;
        return quests.stream().map(quest -> {
            QuestStatus status = finalQuestStatusMap.get(quest.getQuestId());
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
                    .status(status)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public void acceptQuest(long questId, long userId) {
        Quest quest = questRepository.findQuestById(questId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUEST_NOT_FOUND));

        userQuestRepository.findByUserIdAndQuestId(userId, questId).ifPresent(userQuest -> {
            throw new BusinessException(ErrorCode.QUEST_ALREADY_ACCEPTED);
        });

        if (quest.getPreviousQuestId() != null && quest.getPreviousQuestId() > 0) {
            userQuestRepository.findCompletedByUserIdAndQuestId(userId, quest.getPreviousQuestId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PREVIOUS_QUEST_NOT_COMPLETED));
        }

        UserQuest newUserQuest = UserQuest.builder()
                .userId(userId)
                .questId(questId)
                .status(QuestStatus.ACCEPTED)
                .build();
        userQuestRepository.save(newUserQuest);
    }

    @Override
    public void forfeitQuest(long questId, long userId) {
        UserQuest userQuest = userQuestRepository.findByUserIdAndQuestId(userId, questId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUEST_NOT_ACCEPTED));

        if (userQuest.getStatus() != QuestStatus.ACCEPTED) {
            throw new BusinessException(ErrorCode.QUEST_NOT_IN_FORFEITABLE_STATE);
        }

        userQuestRepository.delete(userQuest);
    }

    @Override
    public void completeArrivalQuest(long questId, long userId, QuestCompleteRequestDto request) {
        completeQuestInternal(questId, userId, request, null, null, null);
    }

    @Override
    public void completePhotoQuest(long questId, long userId, MultipartFile imageFile, BigDecimal latitude,
            BigDecimal longitude) throws IOException {
        completeQuestInternal(questId, userId, null, imageFile, latitude, longitude);
    }

    private void completeQuestInternal(long questId, long userId, QuestCompleteRequestDto arrivalRequest,
            MultipartFile photoFile, BigDecimal currentLat, BigDecimal currentLon) {
        Quest quest = questRepository.findQuestById(questId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUEST_NOT_FOUND));
        UserQuest userQuest = userQuestRepository.findByUserIdAndQuestId(userId, questId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUEST_NOT_ACCEPTED));

        if (userQuest.getStatus() == QuestStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.QUEST_ALREADY_COMPLETED);
        }
        if (userQuest.getStatus() != QuestStatus.ACCEPTED) {
            throw new BusinessException(ErrorCode.QUEST_NOT_ACCEPTED);
        }

        switch (quest.getQuestTypeId()) {
            case 1:
                verifyArrivalQuest(quest, arrivalRequest);
                break;
            case 2:
                performPhotoVerification(quest, userId, photoFile, currentLat, currentLon);
                break;
            default:
                break;
        }

        userQuest.setStatus(QuestStatus.COMPLETED);
        userQuest.setCompletedAt(java.time.LocalDateTime.now());
        userQuestRepository.update(userQuest);

        grantQuestRewards(userId, quest);

        // 활동 로그 기록
        activityLogService.logQuestCompletion(userId, quest.getQuestId(), quest.getTitle(), quest.getRewardXp(),
                quest.getRewardPoints());
    }

    private void performPhotoVerification(Quest quest, Long userId, MultipartFile imageFile, BigDecimal currentLat,
            BigDecimal currentLon) {
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

                if (gpsDirectory == null || !gpsDirectory.containsTag(GpsDirectory.TAG_LATITUDE)
                        || !gpsDirectory.containsTag(GpsDirectory.TAG_LONGITUDE)) {
                    throw new BusinessException(ErrorCode.PHOTO_METADATA_MISSING);
                }

                GeoLocation photoLocation = gpsDirectory.getGeoLocation();
                verificationLat = BigDecimal.valueOf(photoLocation.getLatitude());
                verificationLon = BigDecimal.valueOf(photoLocation.getLongitude());

                Date photoTimestamp;
                ExifSubIFDDirectory exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                photoTimestamp = (exifSubIFDDirectory != null)
                        ? exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)
                        : null;

                if (photoTimestamp == null) {
                    throw new BusinessException(ErrorCode.PHOTO_METADATA_MISSING);
                }
                log.info("사진 촬영 시간: {}", photoTimestamp);

                Quest arrivalQuest = questRepository
                        .findFirstByLocationIdAndQuestTypeIdOrderByQuestIdAsc(location.getLocationId(), 1)
                        .orElseThrow(() -> new BusinessException(ErrorCode.ARRIVAL_QUEST_NOT_FOUND));

                UserQuest arrivalUserQuest = userQuestRepository
                        .findByUserIdAndQuestId(userId, arrivalQuest.getQuestId())
                        .filter(uq -> uq.getStatus() == QuestStatus.COMPLETED)
                        .orElseThrow(() -> new BusinessException(ErrorCode.ARRIVAL_QUEST_NOT_COMPLETED));

                Date arrivalMissionCompletionTime = Date
                        .from(arrivalUserQuest.getCompletedAt().atZone(ZoneId.systemDefault()).toInstant());
                log.info("도착 미션 완료 시간: {}", arrivalMissionCompletionTime);

                long timeDifferenceMillis = photoTimestamp.getTime() - arrivalMissionCompletionTime.getTime();
                if (timeDifferenceMillis < 0) {
                    throw new BusinessException(ErrorCode.PHOTO_TIME_BEFORE_ARRIVAL_MISSION);
                } else if (timeDifferenceMillis > TimeUnit.HOURS.toMillis(24)) {
                    throw new BusinessException(ErrorCode.PHOTO_TIME_EXCEEDS_24_HOURS);
                }
            }

            double distance = calculateDistance(verificationLat, verificationLon, location.getLatitude(),
                    location.getLongitude());
            double maxDistance = location.getGpsVerifyRadius() != null ? location.getGpsVerifyRadius() : 50.0;

            if (distance > maxDistance) {
                log.warn("사진 미션 실패 (사용자 {}): 거리 {}m가 요구 반경 {}m보다 큽니다.", userId, String.format("%.2f", distance),
                        maxDistance);
                throw new BusinessException(ErrorCode.DISTANCE_TOO_FAR);
            }

            boolean isLandmarkPhoto = aiService.isPhotoOfLandmark(fileBytes, location.getTitle());
            if (!isLandmarkPhoto) {
                log.warn("사진 미션 실패 (사용자 {}): AI가 사진 내용이 랜드마크 '{}'와 일치하지 않는다고 판단했습니다.", userId, location.getTitle());
                throw new BusinessException(ErrorCode.INVALID_PHOTO_PROOF);
            }

        } catch (ImageProcessingException | IOException e) {
            log.error("사진 파일을 처리하는 중 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

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

    private void verifyArrivalQuest(Quest quest, QuestCompleteRequestDto request) {
        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new BusinessException(ErrorCode.GPS_COORDINATES_REQUIRED);
        }

        LocationWithQuestCountDto location = questRepository.findLocationById(quest.getLocationId())
                .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_FOUND));

        if (location.getLatitude() == null || location.getLongitude() == null) {
            throw new BusinessException(ErrorCode.GPS_COORDINATES_REQUIRED);
        }

        double distance = calculateDistance(BigDecimal.valueOf(request.getLatitude()),
                BigDecimal.valueOf(request.getLongitude()),
                location.getLatitude(), location.getLongitude());

        double maxDistance = location.getGpsVerifyRadius() != null ? location.getGpsVerifyRadius() : 50.0;
        if (distance > maxDistance) {
            throw new BusinessException(ErrorCode.DISTANCE_TOO_FAR);
        }
    }

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

        return R * c * 1000;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InProgressQuestDto> getInProgressQuests(Long userId) {
        List<InProgressQuestDto> acceptedQuests = userQuestRepository.findUserQuestsByStatus(userId,
                QuestStatus.ACCEPTED);
        List<InProgressQuestDto> inProgressQuests = userQuestRepository.findUserQuestsByStatus(userId,
                QuestStatus.IN_PROGRESS);
        List<InProgressQuestDto> allInProgressQuests = new ArrayList<>();
        allInProgressQuests.addAll(acceptedQuests);
        allInProgressQuests.addAll(inProgressQuests);
        return allInProgressQuests;
    }

    @Override
    public int estimateLocationRadius(String locationName, String address) {
        return aiService.estimateLocationRadius(locationName, address);
    }

    @Override
    public List<RadiusEstimateResult> estimateLocationRadiusBatch(List<LocationRadiusRequest> locations) {
        return aiService.estimateLocationRadiusBatch(locations);
    }

    @Override
    @Transactional
    public Map<String, Object> batchRecalculateRadius(List<Long> locationIds) {
        if (locationIds == null || locationIds.isEmpty()) {
            return Map.of("success", false, "message", "No locations selected");
        }

        log.info("Batch recalculating radius for {} locations", locationIds.size());

        // 1. locationIds로 관광지 정보 조회
        List<LocationRadiusRequest> requests = new ArrayList<>();
        for (Long locationId : locationIds) {
            LocationWithQuestCountDto location = questRepository.findLocationById(locationId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_FOUND));

            requests.add(new LocationRadiusRequest(
                    location.getTitle(),
                    location.getAddr1() != null ? location.getAddr1() : ""));
        }

        // 2. 배치 AI 호출
        List<RadiusEstimateResult> results = aiService.estimateLocationRadiusBatch(requests);

        // 3. DB 업데이트
        int updated = 0;
        for (int i = 0; i < locationIds.size() && i < results.size(); i++) {
            Long locationId = locationIds.get(i);
            int newRadius = results.get(i).getRadius();

            questRepository.updateLocationRadius(locationId, newRadius);
            log.info("Updated location {} radius to {}m", locationId, newRadius);
            updated++;
        }

        return Map.of(
                "success", true,
                "updated", updated,
                "total", locationIds.size());
    }

    @Override
    public void updateLocationRadius(Long locationId, int radius) {
        questRepository.updateLocationRadius(locationId, radius);
    }

    @Override
    public void updateLocation(Long locationId, Double latitude, Double longitude, Integer gpsVerifyRadius) {
        questRepository.updateLocation(locationId, latitude, longitude, gpsVerifyRadius);
    }

    @Override
    public void deleteQuest(Long questId) {
        questRepository.deleteQuest(questId);
    }

    @Override
    public void createQuest(Quest quest) {
        questRepository.saveQuest(quest);
    }

    @Override
    @Transactional
    public int generateQuestsFromTourApi(List<Map<String, Object>> items, List<String> types, String areaCode) {
        if (types == null) {
            types = new java.util.ArrayList<>();
        }
        int count = 0;

        // 1. Location ID 시작점 계산
        long locationIdStart = 0;

        try {
            int code = Integer.parseInt(areaCode);
            if (code == 1) { // 서울 (Special Case: 10000~)
                locationIdStart = 10000;
            } else if (code >= 1 && code <= 8) { // Metro Cities: Code * 1000
                locationIdStart = code * 1000L;
            } else if (code >= 31 && code <= 39) { // Provinces: Code * 1000
                locationIdStart = code * 1000L;
            } else {
                // Fallback
                locationIdStart = 90000;
            }
        } catch (NumberFormatException e) {
            locationIdStart = 90000;
        }

        long locationIdEnd = locationIdStart + 999;

        // DB에서 해당 범위 내 Max ID 조회 (기존 잘못된 20000번대 데이터 무시)
        Long maxLocId = questRepository.findMaxLocationIdByRange(locationIdStart, locationIdEnd);
        long nextLocId = (maxLocId < locationIdStart) ? locationIdStart : maxLocId + 1;

        for (Map<String, Object> item : items) {
            // ID 범위 초과 체크
            if (nextLocId > locationIdEnd) {
                log.warn("ID Range Exceeded for AreaCode {}: Max {}", areaCode, locationIdEnd);
                break; // 더 이상 생성 불가
            }
            String title = (String) item.get("title");
            // API 응답 형식이 String일 수 있으므로 안전하게 파싱
            double mapx = Double.parseDouble(String.valueOf(item.get("mapx")));
            double mapy = Double.parseDouble(String.valueOf(item.get("mapy")));
            String addr1 = (String) item.get("addr1"); // TourAPI 주소 정보

            // 1. Location 저장
            LocationWithQuestCountDto loc = new LocationWithQuestCountDto();
            loc.setLocationId(nextLocId);
            loc.setTitle(title);
            loc.setLatitude(BigDecimal.valueOf(mapy));
            loc.setLongitude(BigDecimal.valueOf(mapx));
            loc.setAreaCode(areaCode);
            loc.setAddr1(addr1); // 주소 정보 저장 (AI 반경 산정에 사용)

            // AI 반경이 계산되어 있으면 사용, 없으면 기본값 150
            Integer aiRadius = 150; // 기본값
            if (item.get("aiRadius") != null) {
                try {
                    aiRadius = Integer.parseInt(String.valueOf(item.get("aiRadius")));
                    log.info("AI 반경 적용: {} -> {}m", title, aiRadius);
                } catch (NumberFormatException e) {
                    log.warn("AI 반경 파싱 실패 ({}), 기본값 사용: {}", title, e.getMessage());
                    aiRadius = 150;
                }
            } else {
                log.info("AI 반경 없음 ({}), 기본값 150m 사용", title);
            }
            loc.setGpsVerifyRadius(aiRadius);

            questRepository.saveLocation(loc);

            // 2. Quest 저장 (LocationID * 10 규칙 적용)
            long currentQuestIdBase = nextLocId * 10;
            long nextQuestId = currentQuestIdBase;

            if (types.contains("ARRIVAL")) {
                Quest arrivalQuest = new Quest();
                arrivalQuest.setQuestId(nextQuestId++);
                arrivalQuest.setLocationId(nextLocId);
                arrivalQuest.setQuestTypeId(1); // int
                arrivalQuest.setTitle(title + " 도착");
                arrivalQuest.setDescription(title + "에 도착하여 인증하세요.");
                arrivalQuest.setDifficulty(Difficulty.EASY); // Enum
                arrivalQuest.setRewardXp(50);
                arrivalQuest.setRewardPoints(5);
                arrivalQuest.setRequireGpsVerify(true);
                questRepository.saveQuest(arrivalQuest);
                count++;
            }

            if (types.contains("PHOTO")) {
                Quest photoQuest = new Quest();
                photoQuest.setQuestId(nextQuestId++);
                // ... rest of logic uses nextQuestId which is now bound to location
                photoQuest.setLocationId(nextLocId);
                photoQuest.setQuestTypeId(2); // int
                photoQuest.setTitle(title + " 사진 찍기");
                photoQuest.setDescription(title + "의 멋진 사진을 찍어보세요!");
                photoQuest.setDifficulty(Difficulty.NORMAL); // Enum
                photoQuest.setRewardXp(150);
                photoQuest.setRewardPoints(15);
                photoQuest.setRequireGpsVerify(false);
                photoQuest.setPreviousQuestId(nextQuestId - 1 - (types.contains("ARRIVAL") ? 0 : 1));
                if (!types.contains("ARRIVAL"))
                    photoQuest.setPreviousQuestId(null);
                else
                    photoQuest.setPreviousQuestId(nextQuestId - 2);

                questRepository.saveQuest(photoQuest);
                count++;
            }

            nextLocId++;
        }

        return count;
    }
}