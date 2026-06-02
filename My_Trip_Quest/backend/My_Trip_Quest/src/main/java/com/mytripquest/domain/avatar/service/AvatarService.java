package com.mytripquest.domain.avatar.service;

import com.mytripquest.domain.avatar.dto.AvatarResponse;
import com.mytripquest.domain.avatar.dto.EquipRequest;

public interface AvatarService {
    
    AvatarResponse getAvatar(Long userId);

    void equipItem(Long userId, EquipRequest equipRequest);

	void unequipSlot(Long userId, String slot);

}
