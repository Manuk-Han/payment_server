package com.study.payment.entity;

import com.study.payment.common.PurchaseStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter @Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "purchase")
    private List<PurchaseProduct> purchaseProductList;

    private int totalPrice;

    private LocalDateTime purchaseDateTime;

    private String partnerOrderId;

    @Nullable
    private String errorMessage;

    private PurchaseStatus status;
}
