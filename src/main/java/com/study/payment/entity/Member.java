package com.study.payment.entity;

import com.study.payment.common.Role;
import com.study.payment.dto.OAuthAttributes;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Builder @Getter @Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Member implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    public Member update(OAuthAttributes attributes) {
        this.name = attributes.getName();
        this.email = attributes.getEmail();
        return this;
    }
}
