package com.mytripquest.domain.user.service;

import com.mytripquest.domain.item.entity.Item;
import com.mytripquest.domain.item.repository.ItemMapper;
import com.mytripquest.domain.user.dto.oauth.GoogleOAuth2UserInfo;
import com.mytripquest.domain.user.dto.oauth.KakaoOAuth2UserInfo;
import com.mytripquest.domain.user.dto.oauth.NaverOAuth2UserInfo;
import com.mytripquest.domain.user.dto.oauth.OAuth2UserInfo;
import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
import com.mytripquest.global.error.exception.BusinessException;
import com.mytripquest.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo;

        if (registrationId.equals("kakao")) {
            oAuth2UserInfo = new KakaoOAuth2UserInfo(oAuth2User.getAttributes());
        } else if (registrationId.equals("naver")) {
            oAuth2UserInfo = new NaverOAuth2UserInfo(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        } else {
            // 다른 소셜 로그인(구글 등)을 추가할 경우 여기에 처리
            log.error("Unsupported registrationId: {}", registrationId);
            throw new OAuth2AuthenticationException("Unsupported registrationId: " + registrationId);
        }

        String email = oAuth2UserInfo.getEmail();
        Optional<User> userOptional = userMapper.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            // 신규 사용자 등록
            String nickname = oAuth2UserInfo.getName();
            // 닉네임 중복 시 랜덤 문자열 추가
            if (userMapper.findByNickname(nickname).isPresent()) {
                nickname = nickname + "_" + UUID.randomUUID().toString().substring(0, 4);
            }
            
            user = User.builder()
                    .email(email)
                    .nickname(nickname)
                    .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString())) // 임시 비밀번호
                    .role(User.Role.USER)
                    .points(1000)
                    .build();
            userMapper.save(user);

            // 기본 스킨 아이템 지급
            Item baseSkin = itemMapper.findItemByName("기본 스킨")
                    .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));
            itemMapper.addUserItem(user.getUserId(), baseSkin.getItemId(), true);
        }

     // 1. 기존 속성(읽기 전용)을 수정 가능한 새 Map으로 복사
        Map<String, Object> newAttributes = new HashMap<>(oAuth2UserInfo.getAttributes());
        
        // 2. 이제 안전하게 추가 가능
        newAttributes.put("email", user.getEmail());
        newAttributes.put("userId", user.getUserId()); // 필요하면 ID도 추가

        // 3. 리턴할 때 'newAttributes'를 넣어서 보냄
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                newAttributes, // 👈 여기가 중요! (복사한 맵을 넣어야 함)
                userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
        );
    }
}