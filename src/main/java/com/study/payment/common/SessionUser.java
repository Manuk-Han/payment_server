package com.study.payment.common;

import com.study.payment.entity.Member;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private final String username;
    private final String email;
    private final String password;
    private final Role role;

    public SessionUser(Member member) {
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.role = member.getRole();
    }
}