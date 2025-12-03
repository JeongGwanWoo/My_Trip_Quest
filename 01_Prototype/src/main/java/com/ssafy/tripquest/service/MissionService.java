package com.ssafy.tripquest.service;

import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.ssafy.tripquest.domain.Mission;
import com.ssafy.tripquest.domain.PointLog;
import com.ssafy.tripquest.domain.TouristSpot;
import com.ssafy.tripquest.domain.VerificationResponse;
import com.ssafy.tripquest.mapper.MissionMapper;
import com.ssafy.tripquest.mapper.PointLogMapper;
import com.ssafy.tripquest.mapper.TouristSpotMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionService {

    private final TouristSpotMapper touristSpotMapper;
    private final MissionMapper missionMapper;
    private final PointService pointService;
    private final PointLogMapper pointLogMapper;
    private final AIVisionService aiVisionService; // AIVisionService 주입

    private static final int EARTH_RADIUS_METERS = 6371000;

    public List<Mission> getMissionsBySpotNo(Integer spotNo) {
        return missionMapper.findBySpotNo(spotNo);
    }

    public VerificationResponse verifyArrival(Long userId, Integer spotNo, BigDecimal userLat, BigDecimal userLon) {
        TouristSpot spot = touristSpotMapper.findById(spotNo)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 관광지 번호입니다: " + spotNo));

        log.info("--- 도착 인증 시작 ---");
        log.info("사용자 ID: {}", userId);
        log.info("사용자 위치: 위도={}, 경도={}", userLat, userLon);
        log.info("목표 관광지: '{}' (번호: {})", spot.getTitle(), spotNo);
        log.info("관광지 위치: 위도={}, 경도={}", spot.getLatitude(), spot.getLongitude());
        log.info("요구 반경: {} 미터", spot.getGpsVerifyRange());

        double distance = calculateDistance(userLat, userLon, spot.getLatitude(), spot.getLongitude());
        log.info("계산된 거리: {} 미터", String.format("%.2f", distance));

        if (distance <= spot.getGpsVerifyRange()) {
            log.info("인증 성공: 사용자가 범위 내에 있습니다.");
            Mission mission = missionMapper.findBySpotNoAndMissionType(spotNo, "ARRIVAL")
                    .orElseThrow(() -> new IllegalStateException("해당 관광지에 대한 도착 미션을 찾을 수 없습니다: " + spotNo));

            int newBalance = pointService.earnPoints(
                userId,
                mission.getRewardPoint(),
                PointLog.SourceType.MISSION,
                mission.getMissionId(),
                "'" + spot.getTitle() + "' 도착 미션 완료"
            );
            log.info("--- 도착 인증 종료 ---");
            return new VerificationResponse(true, "도착 미션 성공! 포인트를 획득했습니다!", distance, newBalance);
        } else {
            log.warn("인증 실패: 사용자가 범위 밖에 있습니다.");
            log.info("--- 도착 인증 종료 ---");
            return new VerificationResponse(false, "목표 지점에 더 가까이 가주세요.", distance);
        }
    }

    public Map<String, Object> completePhotoMission(Long userId, Long missionId, MultipartFile file,
                                                    @Nullable BigDecimal currentLat, @Nullable BigDecimal currentLon) {
        log.info("--- 사진 미션 인증 시작 (사용자: {}, 미션: {}) ---", userId, missionId);
        Mission mission = missionMapper.findById(missionId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 미션 ID입니다: " + missionId));
        TouristSpot spot = touristSpotMapper.findById(mission.getSpotNo())
                .orElseThrow(() -> new IllegalStateException("미션이 유효한 관광지에 연결되어 있지 않습니다."));

        if (!"PHOTO".equals(mission.getMissionType())) {
            throw new IllegalStateException("사진 미션이 아닙니다.");
        }
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 제출할 수 없습니다.");
        }

        BigDecimal verificationLat;
        BigDecimal verificationLon;
        String verificationType;

        try {
            byte[] fileBytes = file.getBytes();

            if (currentLat != null && currentLon != null) {
                log.info("--- 제공된 현재 위치로 사진 미션 인증 ---");
                verificationLat = currentLat;
                verificationLon = currentLon;
                verificationType = "현재 위치";
            } else {
                log.info("--- 사진 메타데이터로 사진 미션 인증 ---");
                verificationType = "사진 메타데이터";

                Metadata metadata = com.drew.imaging.ImageMetadataReader.readMetadata(new ByteArrayInputStream(fileBytes));
                GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

                if (gpsDirectory == null || gpsDirectory.getGeoLocation() == null || gpsDirectory.getGeoLocation().isZero()) {
                    return Map.of("success", false, "status", "METADATA_MISSING", "message", "사진에서 위치 정보를 찾을 수 없습니다. 현재 위치로 대신 인증하시겠습니까?");
                }
                
                GeoLocation photoLocation = gpsDirectory.getGeoLocation();
                verificationLat = BigDecimal.valueOf(photoLocation.getLatitude());
                verificationLon = BigDecimal.valueOf(photoLocation.getLongitude());

                Date photoTimestamp;
                ExifSubIFDDirectory exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                if (exifSubIFDDirectory != null) {
                    photoTimestamp = exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                    if (photoTimestamp == null) {
                        photoTimestamp = exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME); // Fallback
                    }
                } else {
                    photoTimestamp = null;
                }

                if (photoTimestamp == null) {
                    return Map.of("success", false, "status", "METADATA_MISSING", "message", "사진에서 촬영 시간 정보를 찾을 수 없습니다.");
                }
                log.info("사진 촬영 시간: {}", photoTimestamp);

                Mission arrivalMission = missionMapper.findBySpotNoAndMissionType(spot.getNo(), "ARRIVAL")
                        .orElseThrow(() -> new IllegalStateException("해당 관광지에 대한 도착 미션을 찾을 수 없습니다."));

                Date arrivalMissionCompletionTime = pointLogMapper.findMissionCompletionTime(userId, arrivalMission.getMissionId())
                        .orElseThrow(() -> new IllegalArgumentException("도착 미션 완료 기록이 없습니다. 먼저 도착 미션을 완료해야 합니다."));

                log.info("도착 미션 완료 시간: {}", arrivalMissionCompletionTime);

                long timeDifferenceMillis = photoTimestamp.getTime() - arrivalMissionCompletionTime.getTime();
                long TWENTY_FOUR_HOURS_MILLIS = 24 * 60 * 60 * 1000L;

                if (timeDifferenceMillis < 0) {
                    throw new IllegalArgumentException("사진 촬영 시간이 도착 미션 완료 시간보다 빠릅니다.");
                } else if (timeDifferenceMillis > TWENTY_FOUR_HOURS_MILLIS) {
                    throw new IllegalArgumentException("사진 촬영 시간이 도착 미션 완료 후 24시간을 초과했습니다.");
                }
            }

            log.info("--- 공통 인증 단계 ---");
            log.info("인증 위치 출처: {}", verificationType);
            log.info("인증 위도={}, 경도={}", verificationLat, verificationLon);
            log.info("목표 관광지: '{}' (번호: {})", spot.getTitle(), spot.getNo());
            log.info("관광지 위치: 위도={}, 경도={}", spot.getLatitude(), spot.getLongitude());
            log.info("요구 반경: {} 미터", spot.getGpsVerifyRange());
            
            double distance = calculateDistance(verificationLat, verificationLon, spot.getLatitude(), spot.getLongitude());
            log.info("계산된 거리: {} 미터", String.format("%.2f", distance));

            if (distance > spot.getGpsVerifyRange()) {
                log.warn("사진 미션 실패 (사용자 {}): 거리 {}m가 요구 반경 {}m보다 큽니다.", userId, String.format("%.2f", distance), spot.getGpsVerifyRange());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "미션 장소에서 올바른 위치에 찍은 사진이 맞는지 확인해주세요.");
                errorResponse.put("distance", distance);
                return errorResponse;
            }

            log.info("--- AI 사진 내용 검증 시작 (사용자: {}, 미션: {}) ---", userId, missionId);
            boolean isLandmarkPhoto = aiVisionService.isPhotoOfLandmark(fileBytes, spot.getTitle());
            if (!isLandmarkPhoto) {
                log.warn("사진 미션 실패 (사용자 {}): AI가 사진 내용이 랜드마크 '{}'와 일치하지 않는다고 판단했습니다.", userId, spot.getTitle());
                return Map.of("success", false, "message", "사진 내용이 미션 장소와 일치하지 않습니다.");
            }
            log.info("--- AI 사진 내용 검증 성공 (사용자: {}, 미션: {}) ---", userId, missionId);

            Path uploadPath = Paths.get("uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            Files.copy(new ByteArrayInputStream(fileBytes), uploadPath.resolve(uniqueFilename));
            log.info("파일 저장됨: {}", uniqueFilename);

            int newBalance = pointService.earnPoints(
                userId,
                mission.getRewardPoint(),
                PointLog.SourceType.MISSION,
                missionId,
                "'" + mission.getTitle() + "' 사진 미션 완료"
            );
            log.info("--- 사진 미션 인증 종료 ---");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "사진 미션 성공! 포인트를 획득했습니다!");
            response.put("newPointBalance", newBalance);
            return response;

        } catch (ImageProcessingException | IOException e) {
            log.error("사진 파일을 처리하는 중 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new RuntimeException("사진 파일을 처리하는 중 오류가 발생했습니다.", e);
        }
    }

    private double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
        double lat1Rad = Math.toRadians(lat1.doubleValue());
        double lon1Rad = Math.toRadians(lon1.doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double lon2Rad = Math.toRadians(lon2.doubleValue());

        double dLon = lon2Rad - lon1Rad;
        double dLat = lat2Rad - lat1Rad;

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }
}