package com.study.payment.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SignInForm {
    private String email;
    private String password;
}
