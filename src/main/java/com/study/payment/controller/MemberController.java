package com.study.payment.controller;

import com.study.payment.common.excepion.CustomException;
import com.study.payment.common.excepion.CustomResponseException;
import com.study.payment.common.jwt.JwtDto;
import com.study.payment.common.jwt.JwtUtil;
import com.study.payment.dto.member.ChangePasswordForm;
import com.study.payment.dto.member.MyPage;
import com.study.payment.dto.member.SignInForm;
import com.study.payment.dto.member.SignupForm;
import com.study.payment.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.servlet.view.RedirectView;

import java.time.Duration;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    private final JwtUtil jwtUtil;

    private static final Duration COOKIE_EXPIRATION = Duration.ofDays(7);

    @PostMapping("/member/signup")
    public ResponseEntity<?> signup(@RequestBody SignupForm signupForm) {
        memberService.signup(signupForm);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/member/signIn")
    public ResponseEntity<?> signIn(@RequestBody SignInForm signInForm) {
        JwtDto jwtDto = memberService.signIn(signInForm);

        return signIn(jwtDto);
    }

    @GetMapping("/member/signIn/oauth")
    public RedirectView signInOAuth(OAuth2User oAuth2User) {
        String accessToken = (String) oAuth2User.getAttributes().get("accessToken");
        String refreshToken = (String) oAuth2User.getAttributes().get("refreshToken");

        String redirectUrl = "http://localhost:3000/home?accessToken=" + accessToken + "&refreshToken=" + refreshToken;

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectUrl);

        return redirectView;
    }

    public ResponseEntity<String> signIn(JwtDto jwtDto) {
        HttpCookie httpCookie = ResponseCookie.from("refresh-token", jwtDto.getRefreshToken())
                .maxAge(COOKIE_EXPIRATION)
                .httpOnly(false)
                .secure(true)
                .path("/")
                .build();

        return ResponseEntity
                .status(CustomResponseException.SUCCESS.getHttpStatus())
//                .header(HttpHeaders.SET_COOKIE, httpCookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtDto.getAccessToken())
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")
                .body("Bearer " + jwtDto.getAccessToken());
    }

    @GetMapping("/member/mypage")
    public ResponseEntity<?> getMyPage(@RequestHeader("Authorization") String token) {
        Long memberId = Long.valueOf(jwtUtil.getUserId(token));

        return ResponseEntity.ok(memberService.getMyPage(memberId));
    }

    @PostMapping("/member/update/mypage")
    public ResponseEntity<?> updateMyPage(@RequestHeader("Authorization") String token, MyPage myPage) {
        Long memberId = Long.valueOf(jwtUtil.getUserId(token));

        memberService.updateMyPage(memberId, myPage);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/member/update/password")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String token, ChangePasswordForm changePasswordForm) {
        Long memberId = Long.valueOf(jwtUtil.getUserId(token));

        memberService.updatePassword(memberId, changePasswordForm);

        return ResponseEntity.ok().build();
    }
}
