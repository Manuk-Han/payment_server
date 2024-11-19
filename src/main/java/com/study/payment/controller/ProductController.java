package com.study.payment.controller;

import com.study.payment.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/product/list")
    public ResponseEntity<?> productList() {
        return ResponseEntity.ok(productService.getProductList());
    }

    @GetMapping("/product/detail/{productId}")
    public ResponseEntity<?> productDetail(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductDetail(productId));
    }

    @GetMapping("/product/rank")
    public ResponseEntity<?> productRank() {
        return ResponseEntity.ok(productService.getProductRank());
    }
}
