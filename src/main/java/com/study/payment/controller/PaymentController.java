package com.study.payment.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping
public class PaymentController {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @PostMapping("/payment")
    public ResponseEntity<?> kakaoPayReady() {
        RestTemplate restTemplate = new RestTemplate();

        String apiUrl = "https://kapi.kakao.com/v1/payment/ready";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> params = new HashMap<>();
        params.put("cid", "TC0ONETIME");
        params.put("partner_order_id", "partner_order_id");
        params.put("partner_user_id", "partner_user_id");
        params.put("item_name", "상품판매");
        params.put("quantity", "1");
        params.put("total_amount", "5500");
        params.put("tax_free_amount", "0");
        params.put("approval_url", "http://localhost:3000/PayResult");
        params.put("cancel_url", "http://localhost:3000/kakaoPay");
        params.put("fail_url", "http://localhost:3000/kakaoPay");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            String redirectUrl = (String) responseBody.get("next_redirect_pc_url");
            String tid = (String) responseBody.get("tid");

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 준비 중 오류 발생");
        }
    }
}
