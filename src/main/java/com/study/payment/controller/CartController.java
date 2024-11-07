package com.study.payment.controller;

import com.study.payment.common.jwt.JwtUtil;
import com.study.payment.dto.cart.CartAddForm;
import com.study.payment.service.CartService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final JwtUtil jwtUtil;

    @GetMapping("/cart/myCart")
    public ResponseEntity<?> myCart(@RequestHeader("Authorization") String requestAccessToken) {
        Long userId = Long.valueOf(jwtUtil.getUserId(requestAccessToken));

        return ResponseEntity.ok(cartService.getMyCart(userId));
    }

    @PostMapping("/cart/addProduct")
    public ResponseEntity<?> addProduct(@RequestHeader("Authorization") String requestAccessToken, CartAddForm cartAddForm) {
        Long userId = Long.valueOf(jwtUtil.getUserId(requestAccessToken));

        cartService.addProductToCart(userId, cartAddForm);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/cart/deleteProduct/{cartId}")
    public ResponseEntity<?> deleteProduct(@RequestHeader("Authorization") String requestAccessToken, @PathVariable Long cartId) {
        Long userId = Long.valueOf(jwtUtil.getUserId(requestAccessToken));

        cartService.deleteProductFromCart(userId, cartId);

        return ResponseEntity.ok().build();
    }
}
