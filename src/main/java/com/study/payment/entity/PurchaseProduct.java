package com.study.payment.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter @Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class PurchaseProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseProductId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private int price;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;
}
