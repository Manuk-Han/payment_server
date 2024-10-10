package com.study.payment.service;

import com.study.payment.common.UserRoles;
import com.study.payment.common.excepion.CustomException;
import com.study.payment.common.excepion.CustomResponseException;
import com.study.payment.common.jwt.JwtDto;
import com.study.payment.common.jwt.JwtUtil;
import com.study.payment.dto.SignInForm;
import com.study.payment.dto.SignupForm;
import com.study.payment.entity.Member;
import com.study.payment.entity.Role;
import com.study.payment.repository.MemberRepository;
import com.study.payment.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    public void signup(SignupForm signupForm) {
        if(memberRepository.existsByEmail(signupForm.getEmail())) {
            throw new CustomException(CustomResponseException.EXIST_EMAIL);
        }

        Role guestRole = roleRepository.findByUserRoles(UserRoles.GUEST);
        Role userRole = roleRepository.findByUserRoles(UserRoles.USER);

        Member member = Member.builder()
                .name(signupForm.getName())
                .email(signupForm.getEmail())
                .password(passwordEncoder.encode(signupForm.getPassword()))
                .roles(new HashSet<>(Arrays.asList(guestRole, userRole)))
                .build();

        memberRepository.save(member);
    }

    public JwtDto signIn(SignInForm  signupForm) {
        Member member = memberRepository.findByEmail(signupForm.getEmail())
                .orElseThrow(() -> new CustomException(CustomResponseException.NOT_FOUND_MEMBER));

        if (!passwordEncoder.matches(signupForm.getPassword(), member.getPassword())) {
            throw new CustomException(CustomResponseException.WRONG_PASSWORD);
        }

        return JwtDto.builder()
                .accessToken(jwtUtil.createAccessToken(member.getUsername(), UserRoles.USER))
                .refreshToken(jwtUtil.createRefreshToken(member.getUsername(), UserRoles.USER))
                .build();
    }
}
