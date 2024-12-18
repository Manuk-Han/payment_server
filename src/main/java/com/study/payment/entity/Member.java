package com.study.payment.entity;

import com.study.payment.common.UserRoles;
import com.study.payment.dto.member.MyPage;
import com.study.payment.dto.member.OAuthAttributes;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Builder @Getter @Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(nullable = false)
    private String name;

    private String email;

    private String password;

    @OneToMany(mappedBy = "member")
    private List<PaymentProduct> paymentProductList;

    @OneToMany(mappedBy = "member")
    private List<Purchase> purchaseList;

    @OneToMany(mappedBy = "member")
    private List<Cart> cartList;

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

    public void update(MyPage myPage) {
        if(this.getProvider() != null && !Objects.equals(myPage.getEmail(), this.email)) {
            throw new IllegalArgumentException("OAuth 회원은 수정할 수 없습니다.");
        }

        this.name = myPage.getName();
        this.email = myPage.getEmail();
    }
}
