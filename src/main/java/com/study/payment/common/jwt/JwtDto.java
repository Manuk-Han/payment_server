package com.study.payment.common.jwt;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class JwtDto {
    private String accessToken;

    private String refreshToken;
}