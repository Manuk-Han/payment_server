package com.study.payment.controller;

import com.study.payment.dto.OAuthAttributes;
import com.study.payment.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/member/signup")
    public ResponseEntity<?> signup(OAuthAttributes attributes) {
        memberService.signup(attributes);

        return ResponseEntity.ok().build();
    }
}
