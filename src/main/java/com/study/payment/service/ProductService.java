package com.study.payment.service;

import com.study.payment.common.excepion.CustomException;
import com.study.payment.common.excepion.CustomResponseException;
import com.study.payment.dto.product.ProductDetailForm;
import com.study.payment.dto.product.ProductForm;
import com.study.payment.dto.rank.RankForm;
import com.study.payment.entity.Product;
import com.study.payment.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final PurchaseProductRepository purchaseProductRepository;

    public List<ProductForm> getProductList() {
        return productRepository.findAll().stream()
                .map(product -> ProductForm.builder()
                        .productId(product.getProductId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    public ProductDetailForm getProductDetail(Long productId) {
        return productRepository.findById(productId)
                .map(product -> ProductDetailForm.builder()
                        .productId(product.getProductId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .stockQuantity(product.getStockQuantity())
                        .build())
                .orElseThrow(() -> new CustomException(CustomResponseException.NOT_FOUND_PRODUCT));
    }

    public List<RankForm> getProductRank() {
        List<Long> productIdList = purchaseProductRepository.findTop5ProductList().stream()
                .map(Product::getProductId)
                .toList();

        return productIdList.stream()
                .map(productId -> productRepository.findById(productId)
                        .map(product -> RankForm.builder()
                                .productId(product.getProductId())
                                .productName(product.getName())
                                .soldCount(purchaseProductRepository.countByProduct(product))
                                .build())
                        .orElseThrow(() -> new CustomException(CustomResponseException.NOT_FOUND_PRODUCT)))
                .collect(Collectors.toList());
    }
}
