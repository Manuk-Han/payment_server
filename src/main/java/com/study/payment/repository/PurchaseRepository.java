package com.study.payment.repository;

import com.study.payment.entity.Member;
import com.study.payment.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Purchase findTopByMemberAndErrorMessageIsNullOrderByPurchaseDateTimeDesc(Member member);
}
