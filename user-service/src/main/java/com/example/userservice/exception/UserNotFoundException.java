package com.example.userservice.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends CustomException {

    public UserNotFoundException() {
        super(AppMessages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    public UserNotFoundException(String userId) {
        super(String.format(AppMessages.USER_NOT_FOUND_BY_ID, userId), HttpStatus.NOT_FOUND);
    }

}
