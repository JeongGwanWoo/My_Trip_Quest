package com.mytripquest.global.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NicknameValidator implements ConstraintValidator<Nickname, String> {

    private static final Pattern KOREAN_PATTERN = Pattern.compile(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*");
    private static final int KOREAN_MAX_LENGTH = 6;
    private static final int ENGLISH_MAX_LENGTH = 12;

    @Override
    public void initialize(Nickname constraintAnnotation) {
    }

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {
        if (nickname == null) {
            return true; // null일 경우 검증을 통과시킴 (선택적 필드)
        }
        if (nickname.trim().isEmpty()) {
            return false; // 비어있는 문자열은 허용하지 않음
        }

        if (KOREAN_PATTERN.matcher(nickname).matches()) {
            return nickname.length() <= KOREAN_MAX_LENGTH;
        } else {
            return nickname.length() <= ENGLISH_MAX_LENGTH;
        }
    }
}
