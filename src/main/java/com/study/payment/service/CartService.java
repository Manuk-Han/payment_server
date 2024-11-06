package com.study.payment.service;

import com.study.payment.common.excepion.CustomException;
import com.study.payment.common.excepion.CustomResponseException;
import com.study.payment.common.jwt.JwtUtil;
import com.study.payment.dto.cart.CartProductForm;
import com.study.payment.dto.product.ProductDetailForm;
import com.study.payment.dto.product.ProductForm;
import com.study.payment.repository.CartRepository;
import com.study.payment.repository.MemberRepository;
import com.study.payment.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final MemberRepository memberRepository;

    private final CartRepository cartRepository;

    public List<CartProductForm> getMyCart(Long userId) {
        return null;
    }
}
