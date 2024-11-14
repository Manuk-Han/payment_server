package com.study.payment.dto.member;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyPage {
    private String email;

    private String name;
}
