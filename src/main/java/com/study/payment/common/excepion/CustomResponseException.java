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
    NOT_FOUND_MEMBER(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),
    EXIST_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    NOT_SUPPORTED_LOGIN_SERVICE(HttpStatus.BAD_REQUEST, "지원하지 않는 로그인 서비스입니다."),
    OAUTH_MEMBER(HttpStatus.BAD_REQUEST, "소셜 로그인 회원입니다."),

    NOT_FOUND_PRODUCT(HttpStatus.BAD_REQUEST, "존재하지 않는 상품입니다."),
    PAYMENT_READY_FAILED(HttpStatus.BAD_REQUEST, "결제 준비에 실패했습니다."),
    PAYMENT_APPROVAL_FAILED(HttpStatus.BAD_REQUEST, "결제 승인에 실패했습니다."),
    PAYMENT_ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "이미 결제 승인된 상품입니다."),

    PURCHASE_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 구매내역입니다."),

    NOT_FOUND_CART(HttpStatus.BAD_REQUEST, "존재하지 않는 장바구니입니다."),
    NOT_FOUND_CART_PRODUCT(HttpStatus.BAD_REQUEST, "존재하지 않는 장바구니 상품입니다."),

    // 401 error
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),

    // 500 error
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류");

    private final HttpStatus httpStatus;
    private final String message;
}
