package com.study.payment.service;

import com.study.payment.common.excepion.CustomException;
import com.study.payment.common.excepion.CustomResponseException;
import com.study.payment.dto.cart.CartAddForm;
import com.study.payment.dto.cart.CartProductForm;
import com.study.payment.dto.product.ProductDetailForm;
import com.study.payment.entity.Cart;
import com.study.payment.entity.Member;
import com.study.payment.entity.Product;
import com.study.payment.repository.CartRepository;
import com.study.payment.repository.MemberRepository;
import com.study.payment.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final MemberRepository memberRepository;

    private final CartRepository cartRepository;

    private final ProductRepository productRepository;

    public List<CartProductForm> getMyCart(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomResponseException.NOT_FOUND_MEMBER));

        return cartRepository.findAllByMember(member).stream()
                .map(cart -> CartProductForm.builder()
                        .cartId(cart.getCartId())
                        .productDetailForm(ProductDetailForm.builder()
                                .productId(cart.getProduct().getProductId())
                                .name(cart.getProduct().getName())
                                .price(cart.getProduct().getPrice())
                                .stockQuantity(cart.getProduct().getStockQuantity())
                                .build())
                        .quantity(cart.getQuantity())
                        .totalPrice(cart.getTotalPrice())
                        .build())
                .collect(Collectors.toList());
    }

    public void addProductToCart(Long userId, CartAddForm cartAddForm) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomResponseException.NOT_FOUND_MEMBER));

        Product product = productRepository.findById(cartAddForm.getProductId())
                .orElseThrow(() -> new CustomException(CustomResponseException.NOT_FOUND_PRODUCT));

        Cart cart = Cart.builder()
                .member(member)
                .product(product)
                .quantity(cartAddForm.getQuantity())
                .totalPrice(product.getPrice() * cartAddForm.getQuantity())
                .cartDateTime(LocalDateTime.now())
                .build();

        cartRepository.save(cart);
    }

    public void deleteProductFromCart(Long userId, Long cartId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(CustomResponseException.NOT_FOUND_MEMBER));

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CustomException(CustomResponseException.NOT_FOUND_CART));

        if (!cart.getMember().equals(member)) {
            throw new CustomException(CustomResponseException.NOT_FOUND_CART_PRODUCT);
        }

        cartRepository.delete(cart);
    }
}
