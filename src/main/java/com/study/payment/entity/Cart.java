package com.study.payment.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter @Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private int totalPrice;

    private LocalDateTime cartDateTime;

    private boolean readyToPurchase;

    public void addQuantity(int quantity) {
        this.quantity += quantity;
        this.totalPrice = this.product.getPrice() * this.quantity;
    }
}
