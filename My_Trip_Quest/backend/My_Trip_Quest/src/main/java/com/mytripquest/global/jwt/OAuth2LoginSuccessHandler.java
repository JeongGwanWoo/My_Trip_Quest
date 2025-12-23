package com.mytripquest.global.jwt;

import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        boolean isNewUser = (boolean) attributes.getOrDefault("isNewUser", false);
        String email = (String) attributes.get("email");
        String targetUrl;

        if (isNewUser) {
            // 신규 사용자인 경우: 임시 등록 토큰 발급
            String provider = (String) attributes.get("provider");
            String name = (String) attributes.get("name");
            String registrationToken = jwtTokenProvider.createRegistrationToken(email, provider, name);
            log.info("New social user. Issuing registration token for: {}", email);

            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/social-login-redirect")
                    .queryParam("registrationToken", registrationToken)
                    .build().toUriString();
        } else {
            // 기존 사용자인 경우: JWT 발급
            User user = userMapper.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid User"));

            String accessToken = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());
            log.info("Successfully generated JWT for social login user: {}", email);

            targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/social-login-redirect")
                    .queryParam("token", accessToken)
                    .build().toUriString();
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
