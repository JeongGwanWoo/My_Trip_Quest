package com.mytripquest.domain.user.service;

import com.mytripquest.domain.user.dto.UserProfileResponseDto;

public interface ProfileService {
    UserProfileResponseDto getProfileData(Long userId);
}
