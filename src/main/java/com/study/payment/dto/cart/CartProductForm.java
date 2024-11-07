package com.study.payment.dto.cart;

import com.study.payment.dto.product.ProductDetailForm;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
@Builder
public class CartProductForm {
    private Long cartId;

    private ProductDetailForm productDetailForm;

    private int quantity;

    private int totalPrice;
}
