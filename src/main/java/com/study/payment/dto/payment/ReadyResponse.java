package com.study.payment.dto.payment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReadyResponse {

    private String tid;
    private String nextRedirectPcUrl;
}