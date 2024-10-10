package com.study.payment.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoles {
    ADMIN("ROLE_ADMIN", "관리자"),
    USER("ROLE_USER", "사용자"),
    GUEST("ROLE_GUEST", "손님");

    private final String key;
    private final String title;
}