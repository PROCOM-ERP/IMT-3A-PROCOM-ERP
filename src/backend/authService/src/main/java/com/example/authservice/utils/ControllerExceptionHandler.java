package com.example.authservice.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
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

    @ExceptionHandler(AccessDeniedException.class) // Http 403
    public ResponseEntity<String> handleAccessDeniedExceptions() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(NoSuchElementException.class) // Http 404
    public ResponseEntity<String> handleNoSuchElementExceptions() {
        return ResponseEntity.notFound().build();
    }

}
