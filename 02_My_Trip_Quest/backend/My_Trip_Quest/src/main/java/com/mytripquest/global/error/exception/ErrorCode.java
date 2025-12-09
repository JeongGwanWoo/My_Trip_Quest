package com.mytripquest.global.error.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Global
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),

    // Quest
    QUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 퀘스트를 찾을 수 없습니다."),
    QUEST_ALREADY_ACCEPTED(HttpStatus.CONFLICT, "이미 수락한 퀘스트입니다."),
    QUEST_ALREADY_COMPLETED(HttpStatus.CONFLICT, "이미 완료된 퀘스트입니다."),
    QUEST_NOT_ACCEPTED(HttpStatus.BAD_REQUEST, "수락하지 않았거나 완료할 수 없는 상태의 퀘스트입니다."),
    PREVIOUS_QUEST_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "선행 퀘스트를 먼저 완료해야 합니다."),
    DISTANCE_TOO_FAR(HttpStatus.BAD_REQUEST, "퀘스트 완료 장소와 너무 멀리 떨어져 있습니다."),
    GPS_COORDINATES_REQUIRED(HttpStatus.BAD_REQUEST, "GPS 좌표가 필요합니다."),
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 장소를 찾을 수 없습니다."),

    // Item
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 아이템을 찾을 수 없습니다."),
    ITEM_NOT_PURCHASABLE(HttpStatus.BAD_REQUEST, "구매할 수 없는 아이템입니다."),
    ITEM_ALREADY_OWNED(HttpStatus.CONFLICT, "이미 소유하고 있는 아이템입니다."),
    INSUFFICIENT_FUNDS(HttpStatus.BAD_REQUEST, "코인이 부족합니다.");


    private final HttpStatus status;
    private final String message;
}
