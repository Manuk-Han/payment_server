package com.study.payment.service;

import com.study.payment.common.excepion.CustomException;
import com.study.payment.common.excepion.CustomResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {
    public void test() {
        System.out.println("test");
        throw new CustomException(CustomResponseException.EXIST_EMAIL);
    }
}
