package com.example.orderservice.exception;

import org.springframework.http.HttpStatus;

import static com.example.orderservice.exception.AppMessages.NOT_FOUND_BY_ID;


public class ResourceNotFoundException extends CustomException {

    public ResourceNotFoundException(String resourceName, String id) {
        super(String.format(NOT_FOUND_BY_ID, resourceName, id), HttpStatus.NOT_FOUND);
    }

}
