package com.study.payment.service;

import com.study.payment.common.excepion.CustomException;
import com.study.payment.common.excepion.CustomResponseException;
import com.study.payment.dto.payment.ApproveForm;
import com.study.payment.dto.payment.ApproveResponse;
import com.study.payment.dto.payment.PaymentForm;
import com.study.payment.dto.payment.ReadyResponse;
import com.study.payment.entity.Member;
import com.study.payment.entity.Purchase;
import com.study.payment.entity.PaymentProduct;
import com.study.payment.entity.Product;
import com.study.payment.repository.MemberRepository;
import com.study.payment.repository.PurchaseRepository;
import com.study.payment.repository.PaymentProductRepository;
import com.study.payment.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final PaymentProductRepository paymentProductRepository;
    private final PurchaseRepository purchaseRepository;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Value("${kakao.api.cid}")
    private String kakaoCid;

    private final String KAKAO_API_URL = "https://open-api.kakaopay.com/online/v1/payment/";
    private final String KAKAO_READY_API_URL = KAKAO_API_URL + "ready";
    private final String KAKAO_APPROVE_API_URL = KAKAO_API_URL + "approve";

    public ReadyResponse payReady(Long userId, PaymentForm paymentForm) {
        Member member = memberRepository.findMemberByMemberId(userId);
        Product product = productRepository.findById(paymentForm.getProductId())
                .orElseThrow(() -> new CustomException(CustomResponseException.NOT_FOUND_PRODUCT));

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", kakaoCid);
        parameters.put("partner_order_id", generateUniqueOrderId());
        parameters.put("partner_user_id", member.getEmail());
        parameters.put("item_name", product.getName());
        parameters.put("quantity", String.valueOf(paymentForm.getQuantity()));
        parameters.put("total_amount", String.valueOf(product.getPrice() * paymentForm.getQuantity()));
        parameters.put("tax_free_amount", "0");
        parameters.put("approval_url", "http://localhost:3000/payment/success");
        parameters.put("cancel_url", "http://localhost:3000/payment/cancel");
        parameters.put("fail_url", "http://localhost:3000/payment/fail");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate template = new RestTemplate();

        Purchase purchase = purchaseRepository.save(Purchase.builder()
                .member(member)
                .product(product)
                .quantity(paymentForm.getQuantity())
                .price(product.getPrice() * paymentForm.getQuantity())
                .purchaseDateTime(LocalDateTime.now())
                .partnerOrderId(parameters.get("partner_order_id"))
                .build());

        try {
            ResponseEntity<ReadyResponse> responseEntity = template.postForEntity(KAKAO_READY_API_URL, requestEntity, ReadyResponse.class);
            log.info("결제준비 응답객체: " + responseEntity.getBody());
            return responseEntity.getBody();
        } catch (Exception e) {
            log.error("KakaoPay 결제 준비 요청 실패", e);
            purchase.setErrorMessage(e.getMessage());
            purchaseRepository.save(purchase);
            throw new CustomException(CustomResponseException.PAYMENT_READY_FAILED);
        }
    }

    public ApproveResponse payApprove(ApproveForm approveForm, Long userId) {
        Member member = memberRepository.findMemberByMemberId(userId);
        Product product = productRepository.findById(approveForm.getProductId())
                .orElseThrow(() -> new CustomException(CustomResponseException.NOT_FOUND_PRODUCT));
        Purchase purchase = purchaseRepository.findTopByMemberAndErrorMessageIsNullOrderByPurchaseDateTimeDesc(member);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", kakaoCid);
        parameters.put("tid", approveForm.getTid());
        parameters.put("partner_order_id", purchase.getPartnerOrderId());
        parameters.put("partner_user_id", member.getEmail());
        parameters.put("pg_token", approveForm.getPgToken());

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate template = new RestTemplate();

        try {
            log.info("KakaoPay 결제 승인 요청 파라미터: " + parameters);
            log.info("KakaoPay 결제 승인 요청 헤더: " + this.getHeaders());

            ApproveResponse approveResponse = template.postForObject(KAKAO_APPROVE_API_URL, requestEntity, ApproveResponse.class);
            log.info("결제승인 응답객체: " + approveResponse);

            PaymentProduct paymentProduct = PaymentProduct.builder()
                    .member(member)
                    .product(product)
                    .quantity(approveForm.getQuantity())
                    .price(product.getPrice() * approveForm.getQuantity())
                    .payedDateTime(LocalDateTime.now())
                    .build();
            paymentProductRepository.save(paymentProduct);

            product.removeStock(approveForm.getQuantity());
            productRepository.save(product);

            return approveResponse;
        } catch (Exception e) {
            log.error("KakaoPay 결제 승인 요청 실패", e);
            throw new CustomException(CustomResponseException.PAYMENT_APPROVAL_FAILED);
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "DEV_SECRET_KEY " + kakaoApiKey);
        headers.set("Content-type", "application/json");

        return headers;
    }

    public String generateUniqueOrderId() {
        return "ORDER-" + UUID.randomUUID();
    }
}