package com.mytripquest.domain.user.service;

import com.mytripquest.domain.item.entity.Item;
import com.mytripquest.domain.item.repository.ItemMapper;
import com.mytripquest.domain.user.dto.UserRequestDto;
import com.mytripquest.domain.user.dto.UserResponseDto;
import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;
import com.mytripquest.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ItemMapper itemMapper; // ItemMapper 주입

    @Transactional
    public void register(UserRequestDto.Register aVar) {
        // 이메일 중복 확인
        if (userMapper.findByEmail(aVar.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        // 닉네임 중복 확인
        if (userMapper.findByNickname(aVar.getNickname()).isPresent()) {
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(aVar.getEmail())
                .passwordHash(passwordEncoder.encode(aVar.getPassword()))
                .nickname(aVar.getNickname())
                .role(User.Role.USER) // 기본 역할은 USER
                .points(1000) // 1000 포인트 지급
                .build();
        userMapper.save(user); // user.getUserId()가 이 시점에 채워짐

        // "기본 스킨" 아이템 지급
        Item baseSkin = itemMapper.findItemByName("기본 스킨")
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));
        
        itemMapper.addUserItem(user.getUserId(), baseSkin.getItemId(), true);
    }

    public String login(UserRequestDto.Login aVar) {
        User user = userMapper.findByEmail(aVar.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        if (!passwordEncoder.matches(aVar.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND); // 참고: 보안을 위해 "잘못된 비밀번호"라고 명시하지 않음
        }

        return jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());
    }

    public UserResponseDto.ProfileResponseDto getProfile(String email) {
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserResponseDto.ProfileResponseDto.from(user);
    }

    public Long findIdByEmail(String email) {
        return userMapper.findIdByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

}