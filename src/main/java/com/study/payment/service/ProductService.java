package com.study.payment.service;

import com.study.payment.common.UserRoles;
import com.study.payment.common.excepion.CustomException;
import com.study.payment.common.excepion.CustomResponseException;
import com.study.payment.common.jwt.JwtDto;
import com.study.payment.common.jwt.JwtUtil;
import com.study.payment.dto.member.SignInForm;
import com.study.payment.dto.member.SignupForm;
import com.study.payment.dto.product.ProductForm;
import com.study.payment.entity.Member;
import com.study.payment.entity.Role;
import com.study.payment.repository.MemberRepository;
import com.study.payment.repository.ProductRepository;
import com.study.payment.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final JwtUtil jwtUtil;

    public List<ProductForm> getProductList() {
        return productRepository.findAll().stream()
                .map(product -> ProductForm.builder()
                        .name(product.getName())
                        .price(product.getPrice())
                        .build())
                .collect(Collectors.toList());
    }
}
