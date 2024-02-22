package com.example.inventoryservice.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class) // Http 400
    public ResponseEntity<String> handleIllegalArgumentExceptions() {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(AccessDeniedException.class) // Http 403
    public ResponseEntity<String> handleAccessDeniedExceptions() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @ExceptionHandler(NoSuchElementException.class) // Http 404
    public ResponseEntity<String> handleNoSuchElementExceptions() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class) // Http 422
    public ResponseEntity<String> handleDataIntegrityViolationExceptions() {
        return ResponseEntity.unprocessableEntity().build();
    }
}
