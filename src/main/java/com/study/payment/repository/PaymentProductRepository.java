package com.study.payment.repository;

import com.study.payment.entity.PaymentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentProductRepository extends JpaRepository<PaymentProduct, Long> {
}
