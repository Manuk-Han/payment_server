package com.study.payment.dto.payment;

import com.fasterxml.jackson.annotation.JsonValue;
import com.study.payment.common.PaymentType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
@Builder
public class PaymentForm {
    private Long productId;

    private int quantity;

    private PaymentType paymentType;
}
