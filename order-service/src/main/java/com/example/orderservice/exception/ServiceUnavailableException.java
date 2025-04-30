package com.example.orderservice.exception;

import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends CustomException {

    public ServiceUnavailableException() {
        super(AppMessages.SERVICE_UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
