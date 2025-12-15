package com.mytripquest.domain.user.dto.oauth;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> responseAttributes;

    @SuppressWarnings("unchecked")
    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.responseAttributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProviderId() {
        return responseAttributes != null ? (String) responseAttributes.get("id") : null;
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return responseAttributes != null ? (String) responseAttributes.get("email") : null;
    }

    @Override
    public String getName() {
        return responseAttributes != null ? (String) responseAttributes.get("nickname") : null;
    }
}
