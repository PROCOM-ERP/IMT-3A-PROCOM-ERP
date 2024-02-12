package com.example.gatewayService.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class) // Http 500
    public ResponseEntity<String> handleAllExceptions() {
        return ResponseEntity.internalServerError().build();
    }

    @ExceptionHandler(IllegalArgumentException.class) // Http 400
    public ResponseEntity<String> handleIllegalArgumentExceptions() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(InsufficientAuthenticationException.class) // Http 401
    public ResponseEntity<String> handleInsufficientAuthenticationExceptions() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
