package com.study.payment.service;

import com.study.payment.common.Role;
import com.study.payment.common.jwt.JwtUtil;
import com.study.payment.dto.OAuthAttributes;
import com.study.payment.entity.Member;
import com.study.payment.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        OAuthAttributes attributes = OAuthAttributes.of(userRequest.getClientRegistration().getRegistrationId(),
                userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(),
                oAuth2User.getAttributes());

        String nameAttributeKey = attributes.getNameAttributeKey();
        if (!oAuth2User.getAttributes().containsKey(nameAttributeKey)) {
            nameAttributeKey = "sub";
        }

        Member member = saveOrUpdate(attributes);
        String accessToken = jwtUtil.createAccessToken(member.getEmail(), member.getRole());
        String refreshToken = jwtUtil.createRefreshToken(member.getEmail(), member.getRole());

        Map<String, Object> userAttributes = new HashMap<>(attributes.getAttributes());
        userAttributes.put("accessToken", accessToken);
        userAttributes.put("refreshToken", refreshToken);

        System.out.println("nameAttributeKey: " + nameAttributeKey);
        System.out.println("userAttributes: " + userAttributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey())),
                userAttributes,
                nameAttributeKey
        );
    }

    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member member = memberRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes))
                .orElse(
                        Member.builder()
                                .name(attributes.getName())
                                .email(attributes.getEmail())
                                .role(Role.USER)
                                .build()
                );

        return memberRepository.save(member);
    }
}
