package com.study.payment.common.excepion;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

@Getter
public class CustomException extends RuntimeException{
    private final LocalDateTime timestamp;
    private final HttpStatusCode statusCode;
    private final String error;
    private final String message;
    private final CustomResponseException customException;

    public CustomException(CustomResponseException customResponseException) {
        this.statusCode = customResponseException.getHttpStatus();
        this.error = customResponseException.getHttpStatus().name();
        this.message = customResponseException.getMessage();
        this.timestamp = LocalDateTime.now();
        this.customException = customResponseException;
    }
}
