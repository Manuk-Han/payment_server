package com.study.payment.common.excepion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomResponseException {

    // 200 error
    SUCCESS(HttpStatus.OK, "성공"),

    // 400 error
    ALREADY_EXIST_ID(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),

    // 500 error
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류");

    private final HttpStatus httpStatus;
    private final String message;
}
