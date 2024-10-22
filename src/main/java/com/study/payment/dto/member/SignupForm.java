package com.study.payment.dto.member;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SignupForm {
    private String name;
    private String email;
    private String password;
}
