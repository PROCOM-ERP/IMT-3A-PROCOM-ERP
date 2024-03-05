package com.example.directoryservice.utils;

import com.example.directoryservice.dto.HttpStatusErrorDto;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    @Value("${security.service.name}")
    private String serviceName;

    private final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(Exception.class) // Http 500
    public ResponseEntity<HttpStatusErrorDto> handleAllExceptions(
            Exception e)
    {
        HttpStatusErrorDto error = HttpStatusErrorDto.builder()
                .message(e.getMessage())
                .build();
        logger.error("Service " + serviceName + " throws an error\n", e);
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class) // Http 400
    public ResponseEntity<HttpStatusErrorDto> handleIllegalArgumentExceptions(
            IllegalArgumentException e)
    {
        HttpStatusErrorDto error = HttpStatusErrorDto.builder()
                .message(e.getMessage())
                .build();
        logger.error("Service " + serviceName + " throws an error\n", e);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ValidationException.class) // Http 400
    public ResponseEntity<HttpStatusErrorDto> handleValidationExceptions(
            ValidationException e)
    {
        HttpStatusErrorDto error = HttpStatusErrorDto.builder()
                .message(e.getMessage())
                .build();
        logger.error("Service " + serviceName + " throws an error\n", e);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // Http 400
    public ResponseEntity<HttpStatusErrorDto> handleMethodArgumentNotValidExceptions(
            MethodArgumentNotValidException e)
    {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        HttpStatusErrorDto error = HttpStatusErrorDto.builder()
                .message(message)
                .build();
        logger.error("Service " + serviceName + " throws an error\n", e);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class) // Http 401
    public ResponseEntity<HttpStatusErrorDto> handleInsufficientAuthenticationExceptions(
            InsufficientAuthenticationException e)
    {
        HttpStatusErrorDto error = HttpStatusErrorDto.builder()
                .message(e.getMessage())
                .build();
        logger.error("Service " + serviceName + " throws an error\n", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class) // Http 403
    public ResponseEntity<HttpStatusErrorDto> handleAccessDeniedExceptions(
            AccessDeniedException e)
    {
        HttpStatusErrorDto error = HttpStatusErrorDto.builder()
                .message(e.getMessage())
                .build();
        logger.error("Service " + serviceName + " throws an error\n", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(NoSuchElementException.class) // Http 404
    public ResponseEntity<HttpStatusErrorDto> handleNoSuchElementExceptions(
            NoSuchElementException e)
    {
        logger.error("Service " + serviceName + " throws an error\n", e);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class) // Http 422
    public ResponseEntity<HttpStatusErrorDto> handleDataIntegrityViolationExceptions(
            DataIntegrityViolationException e)
    {
        HttpStatusErrorDto error = HttpStatusErrorDto.builder()
                .message(e.getMessage())
                .build();
        logger.error("Service " + serviceName + " throws an error\n", e);
        return ResponseEntity.unprocessableEntity().body(error);
    }
}
