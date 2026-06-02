package com.mytripquest.domain.user.service;

import com.mytripquest.domain.quest.dto.QuestStatDto;
import com.mytripquest.domain.quest.repository.UserQuestRepository;
import com.mytripquest.domain.user.dto.AdminUserResponseDto;
import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserMapper userMapper;
    private final UserQuestRepository userQuestRepository;

    public List<AdminUserResponseDto> getAllUsers() {
        return userMapper.findAll().stream()
                .map(AdminUserResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserRole(Long userId, com.mytripquest.domain.user.entity.User.Role role) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new com.mytripquest.global.error.exception.BusinessException(com.mytripquest.global.error.exception.ErrorCode.USER_NOT_FOUND));

        user.setRole(role);
        userMapper.updateUser(user);
    }

    public List<QuestStatDto> getQuestCompletionStats() {
        return userQuestRepository.getQuestCompletionStats();
    }
}
