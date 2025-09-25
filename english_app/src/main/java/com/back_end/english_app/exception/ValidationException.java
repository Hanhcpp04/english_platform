package com.back_end.english_app.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends AppException {
    public ValidationException(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR");
    }

    public ValidationException(String field, String message) {
        super(String.format("Validation failed for field '%s': %s", field, message),
              HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR");
    }
}
