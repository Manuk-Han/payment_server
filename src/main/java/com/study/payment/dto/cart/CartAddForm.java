package com.study.payment.dto.cart;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
@Builder
public class CartAddForm {
    private Long productId;

    private int quantity;

    private int totalPrice;
}
