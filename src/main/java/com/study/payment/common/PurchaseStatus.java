package com.study.payment.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PurchaseStatus {
    PENDING("PENDING", "결제 준비 중"),
    APPROVED("APPROVED", "결제 승인 완료"),
    CANCEL("CANCEL", "결제 취소");

    private final String status;
    private final String message;
}
