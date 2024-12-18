package com.study.payment.dto.product;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
@Builder
public class ProductForm {
    private Long productId;

    private String name;

    private int price;

    private int stockQuantity;
}
