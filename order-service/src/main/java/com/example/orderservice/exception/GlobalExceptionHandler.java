package com.example.orderservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.example.orderservice.exception.AppMessages.ACCESS_DENIED;
import static com.example.orderservice.exception.AppMessages.ACCESS_DENIED_TITLE;
import static com.example.orderservice.exception.AppMessages.CUSTOM_ERROR_TITLE;
import static com.example.orderservice.exception.AppMessages.UNEXPECTED_ERROR_TITLE;
import static com.example.orderservice.exception.AppMessages.VALIDATION_ERROR_TITLE;

@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        logger.warn("Custom exception: {}", e.getMessage());
        return ResponseEntity.status(e.getStatus())
                .body(ErrorResponse.fromException(CUSTOM_ERROR_TITLE, e.getMessage(), e.getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        logger.error("Unhandled exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.fromException(UNEXPECTED_ERROR_TITLE, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        String message = ex.getBindingResult().getFieldError().getDefaultMessage();

        ErrorResponse response = ErrorResponse.fromException(VALIDATION_ERROR_TITLE, message, HttpStatus.BAD_REQUEST);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        logger.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.fromException(ACCESS_DENIED_TITLE, ACCESS_DENIED, HttpStatus.FORBIDDEN));
    }

}
