package com.study.payment.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentType {
    BUY("BUY", "구매"),
    REFUND("REFUND", "환불");

    private final String key;
    private final String title;
}
