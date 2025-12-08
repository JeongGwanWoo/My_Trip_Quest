package com.mytripquest.domain.user.service;

import com.mytripquest.domain.user.dto.UserRequestDto;
import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;
import com.mytripquest.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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
                .build();
        userMapper.save(user);
    }

    public String login(UserRequestDto.Login aVar) {
        User user = userMapper.findByEmail(aVar.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        if (!passwordEncoder.matches(aVar.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND); // 참고: 보안을 위해 "잘못된 비밀번호"라고 명시하지 않음
        }

        return jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());
    }

}