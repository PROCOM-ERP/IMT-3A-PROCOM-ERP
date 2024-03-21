package com.example.directoryservice.utils;

import com.example.directoryservice.dto.HttpStatusErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final CustomLogger logger;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException {

        // Log the error with details
        String message = "Forbidden to access the resource (" + request.getRequestURI() + ").";
        logger.error(message,
                CustomLogger.TAG_JWT,
                "checkTokenValidity",
                HttpStatus.FORBIDDEN);

        // build response
        HttpStatusErrorDto error = HttpStatusErrorDto.builder()
                .message(message)
                .build();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getOutputStream().println(objectMapper.writeValueAsString(error));
    }
}
