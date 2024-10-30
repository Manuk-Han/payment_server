package com.study.payment.service;

import com.study.payment.common.UserRoles;
import com.study.payment.common.jwt.JwtUtil;
import com.study.payment.dto.member.OAuthAttributes;
import com.study.payment.entity.Member;
import com.study.payment.entity.PrincipalDetails;
import com.study.payment.entity.Role;
import com.study.payment.repository.MemberRepository;
import com.study.payment.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Member member = saveOrUpdate(attributes, registrationId);
        String accessToken = jwtUtil.createAccessToken(member.getMemberId(), member.getEmail(), member.getHighestUserRole());
        String refreshToken = jwtUtil.createRefreshToken(member.getMemberId(), member.getEmail(), member.getHighestUserRole());

        Map<String, Object> userAttributes = new HashMap<>(attributes.getAttributes());
        userAttributes.put("accessToken", accessToken);
        userAttributes.put("refreshToken", refreshToken);

        return new PrincipalDetails(member, userAttributes, userNameAttributeName);
    }

    private Member saveOrUpdate(OAuthAttributes attributes, String registrationId) {
        Member member = memberRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes))
                .orElseGet(() -> {
                    Role guestRole = roleRepository.findByUserRoles(UserRoles.GUEST);
                    Role userRole = roleRepository.findByUserRoles(UserRoles.USER);

                    return Member.builder()
                            .name(attributes.getName())
                            .email(attributes.getEmail())
                            .provider(registrationId)
                            .roles(new HashSet<>(Arrays.asList(guestRole, userRole)))
                            .createdDateTime(LocalDateTime.now())
                            .build();
                });

        return memberRepository.save(member);
    }
}
