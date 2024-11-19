package com.study.payment.repository;

import com.study.payment.entity.Cart;
import com.study.payment.entity.Member;
import com.study.payment.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findAllByMember(Member member);

    boolean existsByMemberAndProduct(Member member, Product product);

    Cart findByMemberAndProduct(Member member, Product product);

    void deleteByMemberAndProductAndReadyToPurchaseTrue(Member member, Product product);

    void deleteByMemberAndReadyToPurchaseTrue(Member member);

    List<Cart> findAllByMemberAndReadyToPurchaseTrue(Member member);
}
