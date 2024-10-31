package com.study.payment.common.jwt;

import com.study.payment.common.UserRoles;
import com.study.payment.common.excepion.CustomException;
import com.study.payment.common.excepion.CustomResponseException;
import com.study.payment.entity.Member;
import com.study.payment.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Component
public class JwtUtil {
    private final String USER_ID = "user_id";
    private final String USER_NAME = "user_name";
    private final String ROLE = "role";
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final SecretKey key;
    private final long accessTokenExpTime;
    private final long refreshTokenExpTime;

    private final MemberRepository memberRepository;

    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenExpTime,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenExpTime,
            MemberRepository memberRepository) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
        this.memberRepository = memberRepository;
    }


    public String createAccessToken(Long userId, String userName, UserRoles userRoles) {
        return createToken(userId, userName, userRoles, accessTokenExpTime);
    }

    public String createRefreshToken(Long userId, String userName, UserRoles userRoles) {
        return createToken(userId, userName, userRoles, refreshTokenExpTime);
    }


    private String createToken(Long userId, String userName, UserRoles userRoles, long expireTime) {
        Claims claims = Jwts.claims()
                .add(USER_ID, userId)
                .add(USER_NAME, userName)
                .add(ROLE, userRoles.getTitle())
                .build();

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(tokenValidity.toInstant()))
                .signWith(key)
                .compact();
    }


    public String getUserId(String token) {
        return parseClaims(token.substring(7)).get(USER_ID, String.class);
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

//    public Authentication getAuthentication(String token) {
//        try {
//            Long id = Long.valueOf((String) parseClaims(token).get(USER_ID));
//            String role = parseClaims(token).get(ROLE).toString();
//
//            return new UsernamePasswordAuthenticationToken(
//                        memberRepository.findById(id), "", memberRepository.findById(id).get().getAuthorities());
//
//        } catch (Exception e){
//            throw new CustomException(CustomResponseException.TOKEN_INVALID);
//        }
//    }

    public Authentication getAuthentication(String token) {
        try {
            // parseClaims에서 추출한 클레임을 로그로 확인
            Map<String, Object> claims = parseClaims(token);
            System.out.println("Parsed claims: " + claims);

            // 클레임에서 USER_ID와 ROLE 추출
            Long id = Long.valueOf((String) claims.get(USER_ID));
            String role = claims.get(ROLE).toString();

            // 사용자 조회 및 존재 여부 확인
            Optional<Member> member = memberRepository.findById(id);
            if (member.isEmpty()) {
                throw new CustomException(CustomResponseException.NOT_FOUND_MEMBER);
            }

            return new UsernamePasswordAuthenticationToken(
                    member.get(), "", member.get().getAuthorities());

        } catch (Exception e) {
            throw new CustomException(CustomResponseException.TOKEN_INVALID);
        }
    }


    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser().verifyWith(key)
                    .build().parseSignedClaims(accessToken).getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}