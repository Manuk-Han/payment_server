package com.study.payment.common.jwt;

import com.study.payment.entity.PrincipalDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;

    public CustomOAuth2SuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.createAccessToken(principalDetails.getUsername(), principalDetails.getMember().getHighestUserRole());
        String refreshToken = jwtUtil.createRefreshToken(principalDetails.getUsername(), principalDetails.getMember().getHighestUserRole());

        String redirectUrl = "http://localhost:3000/";
        String PREFIX = "Bearer ";
        String encodedAccessToken = URLEncoder.encode(PREFIX + accessToken, StandardCharsets.UTF_8);
        String encodedRefreshToken = URLEncoder.encode(PREFIX + refreshToken, StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl + "?accessToken=" + encodedAccessToken + "&refreshToken=" + encodedRefreshToken);
    }
}
