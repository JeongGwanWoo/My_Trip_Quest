package com.mytripquest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytripquest.domain.user.dto.UserRequestDto;
import com.mytripquest.domain.user.entity.User;
import com.mytripquest.domain.user.repository.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 각 테스트 후 데이터베이스 변경사항 롤백
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_PASSWORD = "1234";
    
    @Test
    @DisplayName("로그인 성공 통합 테스트 - 제공된 자격 증명 사용")
    public void testLogin_Success_Integration() throws Exception {
        // given
        // 참고: 이 테스트는 데이터베이스에 미리 존재하는 사용자에 의존합니다.
        // 이메일 'test@test.com'과 비밀번호 '1234'를 가진 사용자입니다.
        // 더 나은 격리를 위해, @BeforeEach에서 사용자를 생성하는 것을 고려하세요.
        // 데이터베이스 상태를 보장할 수 없는 경우.
        UserRequestDto.Login loginRequest = new UserRequestDto.Login();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        // when & then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인이 성공적으로 완료되었습니다."))
                .andExpect(jsonPath("$.data.token").isNotEmpty()); // 토큰이 비어있지 않은지 확인
    }

    @Test
    @DisplayName("회원가입 성공 통합 테스트")
    public void testRegister_Success_Integration() throws Exception {
        // given
        UserRequestDto.Register registerRequest = new UserRequestDto.Register();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("newpassword");
        registerRequest.setNickname("newbie");

        // when & then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입이 성공적으로 완료되었습니다."));
    }
}