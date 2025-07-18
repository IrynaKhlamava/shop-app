package com.example.authservice.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends CustomException {
    public AccessDeniedException() {
        super(AppMessages.ACCESS_DENIED, HttpStatus.FORBIDDEN);
    }
}
