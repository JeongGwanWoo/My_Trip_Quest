package com.mytripquest.global.error.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    // 1. 에러 코드만 넘길 때 사용하는 생성자
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // 2. ★ 추가된 생성자: 에러 코드와 원인 예외(e)를 함께 넘길 때 사용
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause); // 부모(RuntimeException)에게 원인(cause)을 전달
        this.errorCode = errorCode;
    }
}