package com.ssafy.tripquest.config;

import com.ssafy.tripquest.service.AIVisionService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class TestConfig {

    @Bean
    @Primary // AIVisionService 타입의 빈이 여러 개 있을 경우 이 빈을 우선적으로 사용하도록 지정
    public AIVisionService mockAiVisionService() {
        // Mockito.mock을 사용하여 AIVisionService의 목 객체를 생성합니다.
        return Mockito.mock(AIVisionService.class);
    }
}
