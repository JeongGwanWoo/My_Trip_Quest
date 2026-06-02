package com.mytripquest.domain.user.dto.oauth;

import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;
    private Map<String, Object> kakaoAccountAttributes;
    private Map<String, Object> profileAttributes;

    @SuppressWarnings("unchecked")
    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccountAttributes = (Map<String, Object>) attributes.get("kakao_account");
        if (this.kakaoAccountAttributes != null) {
            this.profileAttributes = (Map<String, Object>) this.kakaoAccountAttributes.get("profile");
        }
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        return kakaoAccountAttributes != null ? (String) kakaoAccountAttributes.get("email") : null;
    }

    @Override
    public String getName() {
        return profileAttributes != null ? (String) profileAttributes.get("nickname") : null;
    }
}
