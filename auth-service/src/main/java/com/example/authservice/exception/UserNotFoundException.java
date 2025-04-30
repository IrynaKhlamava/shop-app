package com.example.authservice.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends CustomException {

    public UserNotFoundException() {
        super(AppMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

}
