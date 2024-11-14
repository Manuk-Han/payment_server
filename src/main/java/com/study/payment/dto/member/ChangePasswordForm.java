package com.study.payment.dto.member;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ChangePasswordForm {
    String oldPassword;

    String newPassword;
}
