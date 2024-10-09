package com.study.payment.service;

import com.study.payment.common.Role;
import com.study.payment.dto.OAuthAttributes;
import com.study.payment.entity.Member;
import com.study.payment.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public void signup(OAuthAttributes attributes) {
        Member member = Member.builder()
                .name(attributes.getName())
                .email(attributes.getEmail())
                .role(Role.USER)
                .build();

        // 회원 정보 저장
        memberRepository.save(member);
    }
}
