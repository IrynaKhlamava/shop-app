package com.example.productservice.exception;

import org.springframework.http.HttpStatus;

public class FileUploadException extends CustomException {

    public FileUploadException() {
        super(AppMessages.FILE_UPLOAD_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
