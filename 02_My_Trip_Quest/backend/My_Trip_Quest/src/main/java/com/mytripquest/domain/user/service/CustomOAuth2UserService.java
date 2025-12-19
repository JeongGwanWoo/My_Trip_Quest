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
        
        Map<String, Object> newAttributes = new HashMap<>(oAuth2UserInfo.getAttributes());
        newAttributes.put("email", email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            newAttributes.put("isNewUser", false);
            newAttributes.put("userId", user.getUserId());
        } else {
            // 신규 사용자일 경우, isNewUser 플래그와 소셜 프로필 정보를 담아서 반환
            newAttributes.put("isNewUser", true);
            newAttributes.put("provider", registrationId);
            newAttributes.put("name", oAuth2UserInfo.getName()); // 닉네임 제안용
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), // 임시 권한
                newAttributes,
                userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
        );
    }
}