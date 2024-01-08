package com.example.directoryservice.utils;

import com.example.directoryservice.service.EndpointService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.Objects;

public class SharedKeyAuthenticationFilter implements Filter {

    private final String sharedKey;

    private final EndpointService endpointService;
    private final Logger logger = LoggerFactory.getLogger(SharedKeyAuthenticationFilter.class);

    public SharedKeyAuthenticationFilter(String sharedKey, EndpointService endpointService) {
        this.sharedKey = sharedKey;
        this.endpointService = endpointService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authHeader = httpRequest.getHeader("Authorization");
        String path = httpRequest.getRequestURI();
        HttpMethod httpMethod = HttpMethod.valueOf(httpRequest.getMethod());
        logger.info("Authorization : " + authHeader);

        if (isPermitAllEndpoint(path, httpMethod) || isBearerToken(authHeader) || isSharedKeyValid(authHeader)) {
            logger.info("Authorization Result : Authorized");
            chain.doFilter(request, response);
        } else {
            logger.info("Authorization Result : Unauthorized");
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    private boolean isBearerToken(String authHeader) {
        return authHeader != null && authHeader.startsWith("Bearer ");
    }

    private boolean isSharedKeyValid(String authHeader) {
        return authHeader != null && authHeader.equals(sharedKey);
    }

    private boolean isPermitAllEndpoint(String path, HttpMethod httpMethod) {
        return endpointService.getPermitEndpoints().stream().anyMatch(endpoint ->
                Objects.equals(endpoint.getPath(), path) &&
                        Objects.equals(endpoint.getHttpMethod().name(), httpMethod.name()));
    }
}
