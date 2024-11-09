package com.study.payment.dto.history;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PurchaseForm {
    private Long purchaseId;

    private LocalDateTime purchaseDateTime;

    private int totalPrice;

    private List<PurchaseDetailForm> purchaseDetailFormList;
}
