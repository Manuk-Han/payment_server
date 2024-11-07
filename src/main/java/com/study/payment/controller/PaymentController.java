package com.study.payment.controller;

import com.study.payment.common.excepion.CustomException;
import com.study.payment.common.excepion.CustomResponseException;
import com.study.payment.common.jwt.JwtUtil;
import com.study.payment.dto.payment.ApproveForm;
import com.study.payment.dto.payment.CartPaymentForm;
import com.study.payment.dto.payment.PaymentForm;
import com.study.payment.dto.payment.ReadyResponse;
import com.study.payment.entity.Member;
import com.study.payment.entity.Product;
import com.study.payment.repository.MemberRepository;
import com.study.payment.repository.PaymentProductRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class PaymentController {
    private final JwtUtil jwtUtil;
    private final KakaoPayService kakaoPayService;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Value("${kakao.api.cid}")
    private String kakaoCid;

    @PostMapping("/payment/kakaoPayReady")
    public ResponseEntity<?> kakaoPayReady(@RequestHeader("Authorization") String requestAccessToken, @RequestBody PaymentForm paymentForm) {
        Long userId = Long.valueOf(jwtUtil.getUserId(requestAccessToken));

        ReadyResponse readyResponse = kakaoPayService.payReady(userId, paymentForm);

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", readyResponse.getNext_redirect_pc_url());
        response.put("tid", readyResponse.getTid());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment/list/kakaoPayReady")
    public ResponseEntity<?> kakaoPayReady(@RequestHeader("Authorization") String requestAccessToken, @RequestBody List<CartPaymentForm> cartPaymentFormList) {
        Long userId = Long.valueOf(jwtUtil.getUserId(requestAccessToken));

        ReadyResponse readyResponse = kakaoPayService.payReady(userId, cartPaymentFormList);

        Map<String, String> response = new HashMap<>();
        response.put("redirectUrl", readyResponse.getNext_redirect_pc_url());
        response.put("tid", readyResponse.getTid());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment/success")
    public ResponseEntity<?> kakaoSuccess(
            @RequestHeader("Authorization") String requestAccessToken, ApproveForm approveForm) {

        Long userId = Long.valueOf(jwtUtil.getUserId(requestAccessToken));

        kakaoPayService.payApprove(approveForm, userId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/payment/list/success")
    public ResponseEntity<?> kakaoSuccess(
            @RequestHeader("Authorization") String requestAccessToken, List<ApproveForm> approveFormList) {

        Long userId = Long.valueOf(jwtUtil.getUserId(requestAccessToken));

        kakaoPayService.payApprove(approveFormList, userId);

        return ResponseEntity.ok().build();
    }

}