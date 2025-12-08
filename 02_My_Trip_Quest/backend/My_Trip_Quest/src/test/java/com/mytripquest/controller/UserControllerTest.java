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
@Transactional 
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
        // Note: This test relies on a pre-existing user in the database
        // with email 'test@test.com' and password '1234'.
        // For better isolation, consider creating a user in @BeforeEach
        // if the database state cannot be guaranteed.
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
                .andExpect(jsonPath("$.data.token").isNotEmpty()); // Check that token is not empty
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