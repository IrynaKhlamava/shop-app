package com.example.authservice.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends CustomException {
    public EmailAlreadyExistsException() {
        super(AppMessages.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
    }
}