package com.example.orderservice.service;

import com.example.orderservice.model.Endpoint;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class EndpointService {

    private final List<Endpoint> endpoints = Arrays.stream(Endpoint.values()).toList();

    public List<Endpoint> getAllEndpoints() {
        return endpoints;
    }

    public List<Endpoint> getPermitEndpoints() {
        return endpoints.stream().filter(endpoint -> endpoint.getPermission() == null).toList();
    }

}
