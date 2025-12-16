package com.mytripquest.domain.user.service;

import com.mytripquest.domain.quest.entity.QuestStatus;
import com.mytripquest.domain.quest.repository.QuestRepository;
import com.mytripquest.domain.quest.repository.UserQuestRepository;
import com.mytripquest.domain.user.dto.CityProgressDto;
import com.mytripquest.domain.user.dto.UserProfileResponseDto;
import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
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
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserMapper userMapper;
    private final QuestRepository questRepository;
    private final UserQuestRepository userQuestRepository;

    // This is duplicated from QuestServiceImpl, consider moving to a shared config/util class
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

        // 3. Get user rank
        Integer rank = userMapper.findUserRankById(userId);

        // 4. Get city progress
        List<CityProgressDto> cityProgress = new ArrayList<>();
        for (Map.Entry<String, String> entry : AREA_CODES.entrySet()) {
            String cityName = entry.getKey();
            String areaCode = entry.getValue();

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
                .joinedAt(user.getCreatedAt().toLocalDate())
                .completedMissions(completedMissions)
                .totalMissions(totalMissions)
                .rank(rank != null ? rank : 0)
                .cityProgress(cityProgress)
                .build();
    }
}
