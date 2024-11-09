package com.study.payment.service;

import com.study.payment.dto.history.PurchaseForm;
import com.study.payment.entity.Member;
import com.study.payment.repository.MemberRepository;
import com.study.payment.repository.PurchaseProductRepository;
import com.study.payment.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class HistoryService {
    private final MemberRepository memberRepository;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseProductRepository purchaseProductRepository;


}
