package com.example.userservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public CustomException(AppMessages errorMessage, HttpStatus status) {
        super(errorMessage.toString());
        this.status = status;
    }
}