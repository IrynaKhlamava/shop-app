package com.example.authservice.exception;

import org.springframework.http.HttpStatus;

public class GoogleAuthenticationException extends CustomException {

    public GoogleAuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public GoogleAuthenticationException(String message, Throwable cause) {
        super(message, HttpStatus.UNAUTHORIZED, cause);
    }
}
