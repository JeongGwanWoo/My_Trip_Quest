package com.mytripquest.global.error.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Quest
    QUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 퀘스트를 찾을 수 없습니다."),
    QUEST_ALREADY_ACCEPTED(HttpStatus.CONFLICT, "이미 수락한 퀘스트입니다."),
    PREVIOUS_QUEST_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "선행 퀘스트를 먼저 완료해야 합니다.");

    private final HttpStatus status;
    private final String message;
}
