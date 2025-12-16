package com.back_end.english_app.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AppException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }

    public ForbiddenException() {
        super("You don't have permission to access this resource", HttpStatus.FORBIDDEN, "FORBIDDEN");
    }
}
