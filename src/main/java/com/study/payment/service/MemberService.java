package com.study.payment.service;

import com.study.payment.common.Role;
import com.study.payment.dto.OAuthAttributes;
import com.study.payment.dto.SignupForm;
import com.study.payment.entity.Member;
import com.study.payment.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public void signup(SignupForm signupForm) {
        Member member = Member.builder()
                .name(signupForm.getName())
                .email(signupForm.getEmail())
                .role(Role.USER)
                .build();

        memberRepository.save(member);
    }
}
