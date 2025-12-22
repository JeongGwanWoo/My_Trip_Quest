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
import com.mytripquest.global.util.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ItemMapper itemMapper;
    private final EmailService emailService;

    // 인증 코드 저장소 (메모리)
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, Instant> verificationTimestamps = new ConcurrentHashMap<>();
    private static final long VERIFICATION_CODE_EXPIRATION_MINUTES = 5;

    // =================================================================================
    // 1. 인증 코드 발송 로직 (회원가입 / 비밀번호 찾기 분리)
    // =================================================================================

    /**
     * 내부 공통 메서드: 실제 코드를 생성하고 이메일을 발송합니다.
     */
    private void sendCodeInternal(String email, String subject) {
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        verificationCodes.put(email, code);
        verificationTimestamps.put(email, Instant.now());

        emailService.sendVerificationEmail(email, subject, code);
        log.info("Verification code email sent to {}", email);
    }

    /**
     * [회원가입용] 인증코드 전송
     * - 이미 가입된 이메일이면 예외 발생
     */
    public void sendVerificationCode(String email) {
        if (userMapper.findByEmail(email).isPresent()) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        sendCodeInternal(email, "My Trip Quest 회원가입 인증 코드");
    }

    /**
     * [비밀번호 찾기용] 인증코드 전송
     * - 가입되지 않은 이메일이면 예외 발생
     */
    public void sendPasswordResetCode(String email) {
        if (userMapper.findByEmail(email).isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        sendCodeInternal(email, "My Trip Quest 비밀번호 재설정 인증 코드");
    }

    // =================================================================================
    // 2. 인증 코드 검증 로직
    // =================================================================================

    /**
     * [내부 검증용] 회원가입/비밀번호 변경 '완료' 시 호출
     * - 검증 성공 시 코드를 삭제합니다. (1회용)
     */
    private void validateVerificationCode(String email, String code) {
        Instant timestamp = verificationTimestamps.get(email);
        String storedCode = verificationCodes.get(email);

        if (storedCode == null || timestamp == null) {
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        if (Instant.now().isAfter(timestamp.plusSeconds(VERIFICATION_CODE_EXPIRATION_MINUTES * 60))) {
            verificationCodes.remove(email);
            verificationTimestamps.remove(email);
            throw new BusinessException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        if (!storedCode.equals(code)) {
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        // 최종 검증 성공 시 코드 제거 (재사용 방지)
        verificationCodes.remove(email);
        verificationTimestamps.remove(email);
    }

    /**
     * [UI 확인용] 프론트엔드 '인증 확인' 버튼 클릭 시 호출
     * - 코드를 삭제하지 않고 단순히 일치 여부만 true/false로 반환합니다.
     */
    public boolean verifyCode(String email, String code) {
        String storedCode = verificationCodes.get(email);
        Instant timestamp = verificationTimestamps.get(email);

        if (storedCode == null || timestamp == null) {
            return false;
        }

        // 만료 시간 체크
        if (Instant.now().isAfter(timestamp.plusSeconds(VERIFICATION_CODE_EXPIRATION_MINUTES * 60))) {
            verificationCodes.remove(email);
            verificationTimestamps.remove(email);
            return false;
        }

        return storedCode.equals(code);
    }

    // =================================================================================
    // 3. 회원가입 및 로그인
    // =================================================================================

    @Transactional
    public void register(UserRequestDto.Register aVar) {
        // 이메일 인증 코드 검증 (성공 시 코드 삭제됨)
        validateVerificationCode(aVar.getEmail(), aVar.getVerificationCode());

        // 닉네임 중복 확인
        if (userMapper.findByNickname(aVar.getNickname()).isPresent()) {
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(aVar.getEmail())
                .passwordHash(passwordEncoder.encode(aVar.getPassword()))
                .nickname(aVar.getNickname())
                .role(User.Role.USER)
                .points(1000)
                .build();
        userMapper.save(user);

        // 기본 아이템 지급
        Item baseSkin = itemMapper.findItemByName("기본 스킨")
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));

        itemMapper.addUserItem(user.getUserId(), baseSkin.getItemId(), true);
    }

    public String login(UserRequestDto.Login aVar) {
        User user = userMapper.findByEmail(aVar.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(aVar.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        return jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());
    }

    @Transactional
    public String socialSignup(UserRequestDto.SocialSignup request) {
        // 1. 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(request.getRegistrationToken())) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        // 2. 토큰에서 정보 추출
        io.jsonwebtoken.Claims claims = jwtTokenProvider.getClaims(request.getRegistrationToken());
        String email = claims.getSubject();
        String provider = claims.get("provider", String.class);

        // 3. 닉네임 중복 확인
        if (userMapper.findByNickname(request.getNickname()).isPresent()) {
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        if (userMapper.findByEmail(email).isPresent()) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 4. 사용자 생성 및 저장
        User user = User.builder()
                .email(email)
                .nickname(request.getNickname())
                .provider(provider)
                .passwordHash(passwordEncoder.encode(java.util.UUID.randomUUID().toString()))
                .role(User.Role.USER)
                .points(1000)
                .build();
        userMapper.save(user);

        // 5. 기본 아이템 지급
        Item baseSkin = itemMapper.findItemByName("기본 스킨")
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));
        itemMapper.addUserItem(user.getUserId(), baseSkin.getItemId(), true);

        return jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());
    }

    // =================================================================================
    // 4. 비밀번호 재설정 (Reset Password)
    // =================================================================================

    /**
     * 비밀번호 재설정 실행
     * - 인증 코드 검증 후, 비밀번호를 변경합니다.
     */
    @Transactional
    public void resetPassword(UserRequestDto.ResetPassword request) {
        // 1. 사용자 존재 확인
        User user = userMapper.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 인증 코드 검증 (성공 시 코드 삭제됨)
        validateVerificationCode(request.getEmail(), request.getVerificationCode());

        // 3. 비밀번호 변경 및 DB 업데이트
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userMapper.updateUser(user);

        log.info("Password reset successfully for user: {}", request.getEmail());
    }

    // =================================================================================
    // 5. 프로필 관리 및 기타
    // =================================================================================

    public UserResponseDto.ProfileResponseDto getProfile(String email) {
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserResponseDto.ProfileResponseDto.from(user);
    }

    public Long findIdByEmail(String email) {
        return userMapper.findIdByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public boolean checkNicknameAvailability(String nickname) {
        if (!StringUtils.hasText(nickname)) {
            return false;
        }
        return userMapper.findByNickname(nickname).isEmpty();
    }

    @Transactional
    public void updateProfile(String email, UserRequestDto.Update updateDto) {
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        boolean isUpdated = false;
        String newNickname = updateDto.getNickname();

        // 닉네임 변경
        if (StringUtils.hasText(newNickname) && !newNickname.equals(user.getNickname())) {
            userMapper.findByNickname(newNickname).ifPresent(u -> {
                throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
            });
            user.setNickname(newNickname);
            isUpdated = true;
        }

        // 비밀번호 변경 (마이페이지에서)
        String currentPassword = updateDto.getCurrentPassword();
        String newPassword = updateDto.getNewPassword();
        if (StringUtils.hasText(newPassword)) {
            if (!StringUtils.hasText(currentPassword)) {
                throw new BusinessException(ErrorCode.PASSWORD_REQUIRED);
            }
            if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
            }
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            isUpdated = true;
        }

        if (isUpdated) {
            userMapper.updateUser(user);
        }
    }

    @Transactional
    public void deleteUser(String email) {
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        userMapper.deleteById(user.getUserId());
    }
}