package com.example.authservice.service;

import com.example.authservice.model.Endpoint;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class EndpointService {

    private final List<Endpoint> endpoints = Arrays.stream(Endpoint.values()).toList();

    public List<Endpoint> getAllEndpoints() {
        return endpoints;
    }

}
