package com.mytripquest.domain.user.dto;

import lombok.Data;

public class UserRequestDto {

    @Data
    public static class Register {
        private String email;
        private String password;
        private String nickname;
    }

    @Data
    public static class Login {
        private String email;
        private String password;
    }

}
