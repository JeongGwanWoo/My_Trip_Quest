package com.ssafy.tripquest.domain;

import lombok.Getter;

@Getter
public class VerificationResponse {
    /** 인증 성공 여부 */
    private final boolean verified;
    /** 응답 메시지 */
    private final String message;
    /** 계산된 거리 (미터) */
    private final Double distance;
    /** 갱신된 포인트 잔액 */
    private final Integer updatedPointBalance;

    public VerificationResponse(boolean verified, String message, Double distance, Integer updatedPointBalance) {
        this.verified = verified;
        this.message = message;
        this.distance = distance;
        this.updatedPointBalance = updatedPointBalance;
    }

    public VerificationResponse(boolean verified, String message, Double distance) {
        this(verified, message, distance, null);
    }
}
