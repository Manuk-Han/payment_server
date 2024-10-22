package com.study.payment.common.excepion;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    private ResponseEntity<CustomException> handleCustomException(CustomException ce) {
        return ResponseEntity.badRequest().body(ce);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<String> handleGeneralException(Exception e) {
        return ResponseEntity.internalServerError().body(new Exception(e.getMessage()).toString());
    }
}
