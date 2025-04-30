package com.example.authservice.exception;

public class AppMessages {
    public static final String EMAIL_ALREADY_EXISTS = "Email already in use";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String ACCESS_DENIED = "Access Denied";
    public static final String INVALID_PASSWORD = "Invalid password";
    public static final String REFRESH_TOKEN_EXPIRED = "Refresh token expired, please login again";
    public static final String REFRESH_TOKEN_BLACKLISTED = "Refresh token is blacklisted, please login again";
    public static final String TOKEN_EXPIRED = "Token expired. Please refresh your token";
    public static final String INVALID_TOKEN = "Invalid token";
    public static final String VALIDATION_ERROR_TITLE = "Validation Error";
    public static final String ACCESS_DENIED_TITLE = "You do not have permission to perform this action";
    public static final String CUSTOM_ERROR_TITLE = "Custom Error";
    public static final String UNEXPECTED_ERROR_TITLE = "Unexpected Error";
    public static final String LOGOUT_SUCCESS = "Logged out successfully";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String INVALID_OR_EXPIRED_TOKEN = "Invalid or expired token";
    public static final String NO_TOKEN_PROVIDED = "No token provided";
    public static final String GOOGLE_TOKEN_MISSING = "Google ID token is missing";
    public static final String GOOGLE_TOKEN_INVALID = "Invalid Google ID token";
    public static final String GOOGLE_TOKEN_VERIFICATION_FAILED = "Failed to verify Google token";
    public static final String GOOGLE_EMAIL_NOT_VERIFIED = "Email not verified by Google";
}
