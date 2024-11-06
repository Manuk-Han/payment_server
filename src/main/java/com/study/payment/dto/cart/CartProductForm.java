package com.study.payment.dto.cart;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
@Builder
public class CartProductForm {
    private Long cartId;

    private Long productId;

    private String name;

    private int price;

    private int quantity;

    private int totalPrice;
}
