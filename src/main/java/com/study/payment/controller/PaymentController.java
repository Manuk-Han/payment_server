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

//    @PostMapping("/payment/kakaoPayReady")
//    public ResponseEntity<?> kakaoPayReady(@RequestHeader("Authorization") String requestAccessToken, @RequestBody PaymentForm paymentForm) {
//        RestTemplate restTemplate = new RestTemplate();
//
//        String apiUrl = "https://kapi.kakao.com/v1/payment/ready";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "SECRET_KEY " + kakaoApiKey);
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        String userId = jwtUtil.getUserId(requestAccessToken);
//        Member member = memberRepository.findMemberByMemberId(Long.valueOf(userId));
//        Product product = productRepository.findById(paymentForm.getProductId()).orElseThrow(
//                () -> new CustomException(CustomResponseException.NOT_FOUND_PRODUCT));
//
//        String partnerOrderId = generatePartnerOrderId();
//        String approvalUrl = "http://localhost:3000/PayResult";
//        String cancelUrl = "http://localhost:3000/kakaoPay";
//        String failUrl = "http://localhost:3000/kakaoPay";
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("cid", kakaoCid);
//        params.add("partner_order_id", partnerOrderId);
//        params.add("partner_user_id", member.getEmail());
//        params.add("item_name", product.getName());
//        params.add("quantity", String.valueOf(paymentForm.getQuantity()));
//        params.add("total_amount", String.valueOf(product.getPrice() * paymentForm.getQuantity()));
//        params.add("tax_free_amount", "0");
//        params.add("approval_url", approvalUrl);
//        params.add("cancel_url", cancelUrl);
//        params.add("fail_url", failUrl);
//
//        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
//
//        try {
//            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, requestEntity, Map.class);
//
//            Map<String, Object> responseBody = response.getBody();
//            String redirectUrl = (String) responseBody.get("next_redirect_pc_url");
//            String tid = (String) responseBody.get("tid");
//
//            return ResponseEntity.ok(responseBody);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 준비 중 오류 발생");
//        }
//    }
//
//    private String generatePartnerOrderId() {
//        return "order_" + UUID.randomUUID().toString();
//    }
}