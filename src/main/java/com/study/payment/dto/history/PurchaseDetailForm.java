package com.study.payment.dto.history;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseDetailForm {
     private Long productId;

     private int quantity;

     private int price;
}
