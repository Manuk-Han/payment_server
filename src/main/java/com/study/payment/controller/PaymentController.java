package com.study.payment.controller;

import com.study.payment.common.excepion.CustomException;
import com.study.payment.common.excepion.CustomResponseException;
import com.study.payment.common.jwt.JwtUtil;
import com.study.payment.dto.payment.PaymentForm;
import com.study.payment.entity.Member;
import com.study.payment.entity.Product;
import com.study.payment.repository.MemberRepository;
import com.study.payment.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class PaymentController {
    private final ProductRepository productRepository;

    private final MemberRepository memberRepository;

    private final JwtUtil jwtUtil;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Value("${kakao.api.cid}")
    private String kakaoCid;

    @PostMapping("/payment/kakaoPayReady")
    public ResponseEntity<?> kakaoPayReady(@RequestHeader("Authorization") String requestAccessToken, @RequestBody PaymentForm paymentForm) {
        RestTemplate restTemplate = new RestTemplate();

        String apiUrl = "https://kapi.kakao.com/v1/payment/ready";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String userId = jwtUtil.getUserId(requestAccessToken);
        Member member = memberRepository.findMemberByMemberId(Long.valueOf(userId));

        Product product = productRepository.findById(paymentForm.getProductId()).orElseThrow(
                () -> new CustomException(CustomResponseException.NOT_FOUND_PRODUCT));

        String partnerOrderId = generatePartnerOrderId();
        String approvalUrl = "http://localhost:3000/PayResult";
        String cancelUrl = "http://localhost:3000/kakaoPay";
        String failUrl = "http://localhost:3000/kakaoPay";

        Map<String, String> params = new HashMap<>();
        params.put("cid", kakaoCid);
        params.put("partner_order_id", partnerOrderId);
        params.put("partner_user_id", member.getEmail());
        params.put("item_name", product.getName());
        params.put("quantity", String.valueOf(paymentForm.getQuantity()));
        params.put("total_amount", String.valueOf(product.getPrice() * paymentForm.getQuantity()));
        params.put("tax_free_amount", "0");
        params.put("approval_url", approvalUrl);
        params.put("cancel_url", cancelUrl);
        params.put("fail_url", failUrl);

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

    private String generatePartnerOrderId() {
        return "order_" + UUID.randomUUID().toString();
    }

}
