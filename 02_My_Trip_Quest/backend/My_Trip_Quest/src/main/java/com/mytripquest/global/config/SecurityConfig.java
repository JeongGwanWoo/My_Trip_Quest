package com.mytripquest.global.config;

import com.mytripquest.domain.user.service.CustomOAuth2UserService;
import com.mytripquest.global.jwt.JwtAuthenticationFilter;
import com.mytripquest.global.jwt.JwtTokenProvider;
import com.mytripquest.global.jwt.OAuth2LoginFailureHandler;
import com.mytripquest.global.jwt.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        
                        // 👇 [수정됨] 여기에 비밀번호 찾기 관련 주소 3개를 추가했습니다!
                        .requestMatchers(
                                "/api/v1/users/register", 
                                "/api/v1/users/login", 
                                "/api/v1/users/social-signup", 
                                "/api/v1/users/check-nickname",
                                "/api/v1/users/send-verification-code", // 회원가입용
                                
                                // [추가] 비밀번호 찾기 관련 3인방
                                "/api/v1/users/send-reset-code", 
                                "/api/v1/users/verify-code",
                                "/api/v1/users/reset-password",
                                
                                "/login/oauth2/**", 
                                "/oauth2/**"
                        ).permitAll()
                        
                        .requestMatchers(HttpMethod.GET, "/api/v1/quest-map/**", "/api/v1/rankings", "/api/v1/items/shop").permitAll()
                        .requestMatchers("/api/v1/users/register", "/api/v1/users/login", "/api/v1/users/social-signup", "/api/v1/users/check-nickname", "/login/oauth2/**", "/oauth2/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/quest-map/**", "/api/v1/rankings", "/api/v1/items/shop", "/api/v1/tour/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 1. 프론트엔드 주소 허용
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
        
        // 2. 모든 HTTP 메서드 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // 3. 모든 헤더 허용
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "x-auth-token"));
        
        // 4. 자격 증명 허용
        configuration.setAllowCredentials(true);
        
        // 5. 헤더 노출
        configuration.setExposedHeaders(Arrays.asList("Authorization", "x-auth-token"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}