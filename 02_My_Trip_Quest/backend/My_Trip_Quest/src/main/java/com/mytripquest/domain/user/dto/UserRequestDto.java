package com.mytripquest.domain.user.dto;

import lombok.Data;

public class UserRequestDto {

    @Data
    public static class Register {
        private String email;
        private String password;
        private String nickname;
        private String verificationCode;
    }

    @Data
    public static class SendVerificationCode {
        private String email;
    }

    @Data
    public static class Login {
        private String email;
        private String password;
    }

    @Data
    public static class Update {
        private String nickname;
        private String currentPassword;
        private String newPassword;
    }

    @Data
    public static class SocialSignup {
        private String registrationToken;
        private String nickname;
    }

    @Data
    public static class IssueTemporaryPassword {
        private String email;
    }
    
    @Data
    public static class VerifyCode {
        private String email;
        private String code; // 프론트엔드에서 보낸 'code' 필드와 매핑
    }
    
    @Data
    public static class ResetPassword {
        private String email;
        private String verificationCode; // 이메일로 받은 인증 코드
        private String newPassword;      // 사용자가 설정할 새 비밀번호
    }

}
