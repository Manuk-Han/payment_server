package com.study.payment.service;

import com.study.payment.common.PurchaseStatus;
import com.study.payment.common.excepion.CustomException;
import com.study.payment.common.excepion.CustomResponseException;
import com.study.payment.dto.payment.*;
import com.study.payment.entity.*;
import com.study.payment.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final PaymentProductRepository paymentProductRepository;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseProductRepository purchaseProductRepository;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Value("${kakao.api.cid}")
    private String kakaoCid;

    private final String KAKAO_API_URL = "https://open-api.kakaopay.com/online/v1/payment/";
    private final String KAKAO_READY_API_URL = KAKAO_API_URL + "ready";
    private final String KAKAO_APPROVE_API_URL = KAKAO_API_URL + "approve";

    @Transactional
    public ReadyResponse payReady(Long userId, PaymentForm paymentForm) {
        Member member = memberRepository.findMemberByMemberId(userId);
        Product product = productRepository.findById(paymentForm.getProductId())
                .orElseThrow(() -> new CustomException(CustomResponseException.NOT_FOUND_PRODUCT));

        Purchase purchase = purchaseRepository.save(Purchase.builder()
                .member(member)
                .purchaseProductList(new ArrayList<>(Collections.singletonList(PurchaseProduct.builder()
                        .product(product)
                        .quantity(paymentForm.getQuantity())
                        .price(product.getPrice() * paymentForm.getQuantity())
                        .build())))
                .totalPrice(product.getPrice() * paymentForm.getQuantity())
                .purchaseDateTime(LocalDateTime.now())
                .partnerOrderId(generateUniqueOrderId())
                .status(PurchaseStatus.PENDING)
                .build());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", kakaoCid);
        parameters.put("partner_order_id", purchase.getPartnerOrderId());
        parameters.put("partner_user_id", member.getEmail());
        parameters.put("item_name", product.getName());
        parameters.put("quantity", String.valueOf(paymentForm.getQuantity()));
        parameters.put("total_amount", String.valueOf(product.getPrice() * paymentForm.getQuantity()));
        parameters.put("tax_free_amount", "0");
        parameters.put("approval_url", "http://localhost:3000/payment/success?purchaseId=" + purchase.getPurchaseId());
        parameters.put("cancel_url", "http://localhost:3000/payment/cancel");
        parameters.put("fail_url", "http://localhost:3000/payment/fail");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate template = new RestTemplate();

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

    @Transactional
    public ReadyResponse payReady(Long userId, List<CartPaymentForm> cartPaymentFormList) {
        Member member = memberRepository.findMemberByMemberId(userId);

        int totalAmount = 0;
        StringBuilder itemNames = new StringBuilder();
        List<PurchaseProduct> purchaseProductList = new ArrayList<>();

        for (CartPaymentForm cartPaymentForm : cartPaymentFormList) {
            Product product = productRepository.findById(cartPaymentForm.getProductId())
                    .orElseThrow(() -> new CustomException(CustomResponseException.NOT_FOUND_PRODUCT));

            int productTotalPrice = product.getPrice() * cartPaymentForm.getQuantity();
            totalAmount += productTotalPrice;

            if (!itemNames.isEmpty()) {
                itemNames.append(", ");
            }
            itemNames.append(product.getName());

            PurchaseProduct purchaseProduct = PurchaseProduct.builder()
                    .product(product)
                    .quantity(cartPaymentForm.getQuantity())
                    .price(productTotalPrice)
                    .build();

            product.removeStock(cartPaymentForm.getQuantity());
            purchaseProductList.add(purchaseProduct);

            purchaseProductRepository.save(purchaseProduct);

            Cart cart = cartRepository.findById(cartPaymentForm.getCartId())
                    .orElseThrow(() -> new CustomException(CustomResponseException.NOT_FOUND_CART));
            cart.setReadyToPurchase(true);
            cartRepository.save(cart);
        }

        Purchase purchase = purchaseRepository.save(Purchase.builder()
                .member(member)
                .purchaseProductList(purchaseProductList)
                .totalPrice(totalAmount)
                .purchaseDateTime(LocalDateTime.now())
                .partnerOrderId(generateUniqueOrderId())
                .status(PurchaseStatus.PENDING)
                .build());

        purchaseProductList.forEach(purchaseProduct -> purchaseProduct.setPurchase(purchase));
        purchaseProductRepository.saveAll(purchaseProductList);
        purchaseRepository.save(purchase);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", kakaoCid);
        parameters.put("partner_order_id", purchase.getPartnerOrderId());
        parameters.put("partner_user_id", member.getEmail());
        parameters.put("item_name", itemNames.toString());
        parameters.put("quantity", String.valueOf(cartPaymentFormList.size()));
        parameters.put("total_amount", String.valueOf(totalAmount));
        parameters.put("tax_free_amount", "0");
        parameters.put("approval_url", "http://localhost:3000/payment/success?purchaseId=" + purchase.getPurchaseId());
        parameters.put("cancel_url", "http://localhost:3000/payment/cancel");
        parameters.put("fail_url", "http://localhost:3000/payment/fail");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate template = new RestTemplate();
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

    @Transactional
    public ApproveResponse payApprove(ApproveForm approveForm, Long userId) {
        Member member = memberRepository.findMemberByMemberId(userId);
        Purchase purchase = purchaseRepository.findTopByMemberAndErrorMessageIsNullOrderByPurchaseDateTimeDesc(member);

        if ("approved".equals(purchase.getStatus())) {
            throw new CustomException(CustomResponseException.PAYMENT_ALREADY_APPROVED);
        }

        Map<String, String> parameters = new HashMap<>();
        parameters.put("cid", kakaoCid);
        parameters.put("tid", approveForm.getTid());
        parameters.put("partner_order_id", purchase.getPartnerOrderId());
        parameters.put("partner_user_id", member.getEmail());
        parameters.put("pg_token", approveForm.getPgToken());

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate template = new RestTemplate();

        try {
            ApproveResponse approveResponse = template.postForObject(KAKAO_APPROVE_API_URL, requestEntity, ApproveResponse.class);
            purchase.setStatus(PurchaseStatus.APPROVED); // 승인 완료 후 상태 변경
            purchaseRepository.save(purchase);

            for (PurchaseProduct purchaseProduct : purchase.getPurchaseProductList()) {
                Product product = purchaseProduct.getProduct();

                PaymentProduct paymentProduct = PaymentProduct.builder()
                        .member(member)
                        .product(product)
                        .quantity(purchaseProduct.getQuantity())
                        .price(purchaseProduct.getPrice())
                        .payedDateTime(LocalDateTime.now())
                        .build();
                paymentProductRepository.save(paymentProduct);

                product.removeStock(purchaseProduct.getQuantity());
                productRepository.save(product);
            }

            deleteCart(member, purchase.getPurchaseProductList().get(0).getProduct());

            return approveResponse;
        } catch (Exception e) {
            log.error("KakaoPay 결제 승인 요청 실패", e);

            purchase.setErrorMessage(e.getMessage());
            purchaseRepository.save(purchase);

            throw new CustomException(CustomResponseException.PAYMENT_APPROVAL_FAILED);
        }
    }

    @Transactional
    public void deleteCart(Member member, Product product) {
        cartRepository.deleteByMemberAndProductAndReadyToPurchaseTrue(member, product);
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
