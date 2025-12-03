package com.mytripquest.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
	private final boolean success;
    private final String message;
    private final T data; // 실제 응답 데이터 (유저, 퀘스트 목록 등)

    /**
     * 성공 응답을 생성하는 정적 팩토리 메서드
     * HTTP Status는 200 OK를 사용하며, 메시지는 기본값 "요청 성공"을 사용합니다.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청 성공", data);
    }

    /**
     * 실패 응답을 생성하는 정적 팩토리 메서드
     * data는 null이며, 실패 메시지를 받아 처리합니다.
     */
    public static <T> ApiResponse<T> failure(String message) {
        // 실패 시 data는 null을 반환합니다. 
        return new ApiResponse<>(false, message, null);
    }
    
    // 필요하다면, data 없이 성공 메시지만 보낼 때 사용 (HTTP 204 No Content 등에 활용)
    public static ApiResponse<?> successWithoutData() {
        return new ApiResponse<>(true, "요청 성공", null);
    }
}
