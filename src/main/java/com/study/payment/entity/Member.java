package com.study.payment.entity;

import com.study.payment.common.UserRoles;
import com.study.payment.dto.member.OAuthAttributes;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

@Entity
@Builder @Getter @Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Member implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "member_role",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @Transient
    private Collection<SimpleGrantedAuthority> authorities;

    private String provider;

    private LocalDateTime createdDateTime;

    public Member update(OAuthAttributes attributes) {
        this.name = attributes.getName();
        this.email = attributes.getEmail();
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        this.getRoles().forEach(role -> {
            String roleName = role.getUserRoles().getKey();
            authorities.add(new SimpleGrantedAuthority(roleName));
        });
        return authorities;
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    public UserRoles getHighestUserRole() {
        return this.roles.stream()
                .map(Role::getUserRoles)
                .min(Comparator.comparingInt(Enum::ordinal))
                .orElse(UserRoles.GUEST);
    }
}
