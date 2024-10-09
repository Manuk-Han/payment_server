package com.study.payment.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SignupForm {
    private String name;
    private String email;
    private String password;
}
