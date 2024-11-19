package com.study.payment.dto.rank;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter @Setter
@Builder
public class RankForm {
    private Long productId;

    private String productName;

    private int soldCount;
}
