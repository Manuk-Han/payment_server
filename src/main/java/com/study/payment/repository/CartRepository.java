package com.study.payment.repository;

import com.study.payment.entity.Cart;
import com.study.payment.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findAllByMember(Member member);
}
