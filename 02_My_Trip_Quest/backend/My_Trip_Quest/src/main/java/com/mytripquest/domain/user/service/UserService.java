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
import org.springframework.util.StringUtils;

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

		User user = User.builder().email(aVar.getEmail()).passwordHash(passwordEncoder.encode(aVar.getPassword()))
				.nickname(aVar.getNickname()).role(User.Role.USER) // 기본 역할은 USER
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
		User user = userMapper.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		return UserResponseDto.ProfileResponseDto.from(user);
	}

	public Long findIdByEmail(String email) {
		return userMapper.findIdByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	}

	public boolean checkNicknameAvailability(String nickname) {
		if (!StringUtils.hasText(nickname)) {
			return false;
		}
		return userMapper.findByNickname(nickname).isEmpty();
	}

	@Transactional
	public void updateProfile(String email, UserRequestDto.Update updateDto) {
		User user = userMapper.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		boolean isUpdated = false;
		String newNickname = updateDto.getNickname();
		if (StringUtils.hasText(newNickname) && !newNickname.equals(user.getNickname())) {
			userMapper.findByNickname(newNickname).ifPresent(u -> {
				throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
			});
			user.setNickname(newNickname);
			isUpdated = true;
		}

		String currentPassword = updateDto.getCurrentPassword();
		String newPassword = updateDto.getNewPassword();
		if (StringUtils.hasText(newPassword)) { // 새 비밀번호를 입력했다면
			if (!StringUtils.hasText(currentPassword)) {
				throw new BusinessException(ErrorCode.PASSWORD_REQUIRED); // "현재 비밀번호를 입력해주세요"
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
		User user = userMapper.findByEmail(email).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		userMapper.deleteById(user.getUserId());
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

		// 4. 이메일 중복 확인 (혹시 모를 경우)
		if (userMapper.findByEmail(email).isPresent()) {
			throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}

		// 5. 사용자 생성 및 저장
		User user = User.builder()
				.email(email)
				.nickname(request.getNickname())
				.provider(provider) // 소셜 로그인 제공자 정보 추가
				.passwordHash(passwordEncoder.encode(java.util.UUID.randomUUID().toString())) // 임시 비밀번호
				.role(User.Role.USER)
				.points(1000)
				.build();
		userMapper.save(user);

		// 6. 기본 아이템 지급
		Item baseSkin = itemMapper.findItemByName("기본 스킨")
				.orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));
		itemMapper.addUserItem(user.getUserId(), baseSkin.getItemId(), true);

		// 7. 최종 JWT 토큰 발급
		return jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());
	}
}
