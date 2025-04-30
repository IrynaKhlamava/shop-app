package com.example.authservice.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends CustomException {
    public ValidationException() {
        super(AppMessages.INVALID_PASSWORD, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
