package com.study.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    private String name;

    private int price;

    private int stockQuantity;

    @OneToMany(mappedBy = "product")
    private List<PaymentProduct> paymentProductList;

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new IllegalStateException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
