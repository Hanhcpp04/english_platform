package com.back_end.english_app.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AppException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }

    public UnauthorizedException() {
        super("Access denied. Please login to continue", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }
}
