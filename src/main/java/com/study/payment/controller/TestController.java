package com.study.payment.controller;

import com.study.payment.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @GetMapping("/test/test")
    public ResponseEntity<?> test() {
        testService.test();

        return ResponseEntity.ok().build();
    }
}
