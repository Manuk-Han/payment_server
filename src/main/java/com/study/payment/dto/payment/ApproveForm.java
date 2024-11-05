package com.study.payment.dto.payment;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@Getter
@Setter
@Builder
public class ApproveForm {
    String pgToken;

    String tid;

    Long productId;

    int quantity;
}
