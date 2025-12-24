package com.mytripquest.global.validation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = NicknameValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Nickname {
    String message() default "닉네임은 한글 6자, 영문 12자 이내로 입력해주세요.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
