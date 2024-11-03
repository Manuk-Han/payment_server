package com.study.payment.controller;

import com.study.payment.common.excepion.CustomException;
import com.study.payment.common.excepion.CustomResponseException;
import com.study.payment.common.jwt.JwtUtil;
import com.study.payment.dto.payment.PaymentForm;
import com.study.payment.entity.Member;
import com.study.payment.entity.Product;
import com.study.payment.repository.MemberRepository;
import com.study.payment.repository.ProductRepository;
import com.study.payment.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class PaymentController {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final KakaoPayService kakaoPayService;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Value("${kakao.api.cid}")
    private String kakaoCid;

    @PostMapping("/payment/kakaoPayReady")
    public ResponseEntity<?> kakaoPayReady(@RequestHeader("Authorization") String requestAccessToken, @RequestBody PaymentForm paymentForm) {
        Long userId = Long.valueOf(jwtUtil.getUserId(requestAccessToken));

        kakaoPayService.payReady(userId, paymentForm);

        return ResponseEntity.ok().build();
    }
}