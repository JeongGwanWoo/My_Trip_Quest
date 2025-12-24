package com.mytripquest.domain.user.service;

import com.mytripquest.domain.quest.entity.QuestStatus;
import com.mytripquest.domain.quest.repository.QuestRepository;
import com.mytripquest.domain.quest.repository.UserQuestRepository;
import com.mytripquest.domain.user.dto.CityProgressDto;
import com.mytripquest.domain.user.dto.LevelProgressDto;
import com.mytripquest.domain.user.dto.UserProfileResponseDto;
import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
import com.mytripquest.domain.user.util.LevelUtil;
import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserMapper userMapper;
    private final QuestRepository questRepository;
    private final UserQuestRepository userQuestRepository;

    // This is duplicated from QuestServiceImpl, consider moving to a shared
    // config/util class
    private static final Map<String, String> AREA_CODES;

    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("서울특별시", "1");
        aMap.put("광주광역시", "5");
        AREA_CODES = Collections.unmodifiableMap(aMap);
    }

    @Override
    public UserProfileResponseDto getProfileData(Long userId) {
        // 1. Get User for join date
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. Get mission counts
        long totalMissions = questRepository.countAll();
        long completedMissions = userQuestRepository.countByUserIdAndStatus(userId, QuestStatus.COMPLETED);
        long acceptedMissions = userQuestRepository.countByUserIdAndStatus(userId, QuestStatus.ACCEPTED);
        long inProgressMissions = userQuestRepository.countByUserIdAndStatus(userId, QuestStatus.IN_PROGRESS);
        long ongoingMissions = acceptedMissions + inProgressMissions;

        // 3. Get user rank
        Integer rank = userMapper.findUserRankById(userId)
                .map(userRankDto -> (int) userRankDto.getRank())
                .orElse(0);

        // 4. Get level progress
        LevelProgressDto levelProgress = LevelUtil.getLevelProgress(user.getTotalXp());

        // 5. Get city progress
        // 5. Get city progress (Only for areas appearing in Quests)
        List<String> activeAreaCodes = questRepository.findDistinctAreaCodes();
        List<CityProgressDto> cityProgress = new ArrayList<>();

        for (String areaCode : activeAreaCodes) {
            String cityName = getAreaName(areaCode);

            int totalQuestsInArea = questRepository.countQuestsByArea(areaCode);
            int completedQuestsInArea = userQuestRepository.countCompletedQuestsByArea(userId, areaCode);

            cityProgress.add(CityProgressDto.builder()
                    .cityName(cityName)
                    .areaCode(areaCode)
                    .completedQuests(completedQuestsInArea)
                    .totalQuests(totalQuestsInArea)
                    .build());
        }

        return UserProfileResponseDto.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .joinedAt(user.getCreatedAt().toLocalDate())
                .points(user.getPoints())
                .levelProgress(levelProgress)
                .completedMissions(completedMissions)
                .ongoingMissions(ongoingMissions)
                .totalMissions(totalMissions)
                .rank(rank != null ? rank : 0)
                .cityProgress(cityProgress)
                .build();
    }

    private String getAreaName(String areaCode) {
        switch (areaCode) {
            case "1":
                return "서울특별시";
            case "2":
                return "인천광역시";
            case "3":
                return "대전광역시";
            case "4":
                return "대구광역시";
            case "5":
                return "광주광역시";
            case "6":
                return "부산광역시";
            case "7":
                return "울산광역시";
            case "8":
                return "세종특별자치시";
            case "31":
                return "경기도";
            case "32":
                return "강원특별자치도";
            case "33":
                return "충청북도";
            case "34":
                return "충청남도";
            case "35":
                return "경상북도";
            case "36":
                return "경상남도";
            case "37":
                return "전북특별자치도";
            case "38":
                return "전라남도";
            case "39":
                return "제주특별자치도";
            default:
                return "알 수 없는 지역 (" + areaCode + ")";
        }
    }
}
