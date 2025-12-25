package com.tandem.auth_service.api.error;

import java.util.Map;

import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tandem.auth_service.api.error.exceptions.VerificationCodeInvalidException;

import org.springframework.http.HttpStatus;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        return Map.of(
            "error", "VALIDATION_ERROR",
            "details", ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList()
        );
    }

    // Обработка кастомного исключения для неверного кода верификации
    @ExceptionHandler(VerificationCodeInvalidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleInvalidVerificationCode(VerificationCodeInvalidException ex) {
        return Map.of(
            "error", "INVALID_VERIFICATION_CODE",
            "message", ex.getMessage()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleIllegalState(IllegalStateException ex) {
        return Map.of(
            "error", "ILLEGAL_STATE",
            "message", ex.getMessage()
        );
    }

}
