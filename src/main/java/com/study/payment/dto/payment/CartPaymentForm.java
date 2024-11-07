package com.study.payment.dto.payment;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
@Builder
public class CartPaymentForm {
    private Long cartId;

    private Long productId;

    private int quantity;
}
