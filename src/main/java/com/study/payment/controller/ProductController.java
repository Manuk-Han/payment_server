package com.study.payment.controller;

import com.study.payment.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/product/list")
    public ResponseEntity<?> productList() {
        return ResponseEntity.ok(productService.getProductList());
    }
}
