package com.example.directoryservice.utils;

import com.example.directoryservice.annotation.LogError;
import com.example.directoryservice.dto.HttpStatusErrorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler {

    /* Constants */
    public static final String ERROR_DEFAULT_MSG_HTTP_500 =
            "The server has encountered an unexpected error.";
    public static final String ERROR_DEFAULT_MSG_HTTP_400 =
            "Inputs don't respect a specific format.";
    public static final String ERROR_DEFAULT_MSG_HTTP_401 =
            "Authentication missing or expired.";
    public static final String ERROR_DEFAULT_MSG_HTTP_403 =
            "Forbidden to access the resource.";

    /* Public Methods */

    @ExceptionHandler(Exception.class) // Http 500
    @LogError(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<HttpStatusErrorDto> handleAllExceptions(
            Exception e)
    {
        HttpStatusErrorDto error = HttpStatusErrorDto.builder()
                .message(ERROR_DEFAULT_MSG_HTTP_500)
                .build();
        return ResponseEntity.internalServerError().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class) // Http 400
    @LogError(httpStatus = HttpStatus.BAD_REQUEST)
    public ResponseEntity<HttpStatusErrorDto> handleIllegalArgumentExceptions(
            IllegalArgumentException e)
    {
        HttpStatusErrorDto error = HttpStatusErrorDto.builder()
                .message(e.getMessage())
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // Http 400
    @LogError(httpStatus = HttpStatus.BAD_REQUEST)
    public ResponseEntity<HttpStatusErrorDto> handleMethodArgumentNotValidExceptions(
            MethodArgumentNotValidException e)
    {
        // retrieve all incorrect fields
        Map<String, String> fields = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(f -> fields.put(f.getField(), f.getDefaultMessage()));

        // build and sent Http Response
        HttpStatusErrorDto error = HttpStatusErrorDto.builder()
                .message(ERROR_DEFAULT_MSG_HTTP_400)
                .fields(fields)
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class) // Http 401
    @LogError(httpStatus = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<HttpStatusErrorDto> handleInsufficientAuthenticationExceptions(
            InsufficientAuthenticationException e)
    {
        HttpStatusErrorDto error = HttpStatusErrorDto.builder()
                .message(ERROR_DEFAULT_MSG_HTTP_401)
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class) // Http 403
    @LogError(httpStatus = HttpStatus.FORBIDDEN)
    public ResponseEntity<HttpStatusErrorDto> handleAccessDeniedExceptions(
            AccessDeniedException e)
    {
        HttpStatusErrorDto error = HttpStatusErrorDto.builder()
                .message(ERROR_DEFAULT_MSG_HTTP_403)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(NoSuchElementException.class) // Http 404
    @LogError(httpStatus = HttpStatus.NOT_FOUND)
    public ResponseEntity<HttpStatusErrorDto> handleNoSuchElementExceptions(
            NoSuchElementException e)
    {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class) // Http 422
    @LogError(httpStatus = HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<HttpStatusErrorDto> handleDataIntegrityViolationExceptions(
            DataIntegrityViolationException e)
    {
        HttpStatusErrorDto error = HttpStatusErrorDto.builder()
                .message(e.getMessage())
                .build();
        return ResponseEntity.unprocessableEntity().body(error);
    }
}
