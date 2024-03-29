package com.example.orderservice.service;

import com.example.orderservice.annotation.LogExecutionTime;
import com.example.orderservice.model.Provider;
import com.example.orderservice.repository.ProviderRepository;
import com.example.orderservice.utils.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderRepository providerRepository;

    /* Public Methods */

    @LogExecutionTime(description = "Retrieve all providers.",
            tag = CustomLogger.TAG_ORDERS)
    public Set<Provider> getAllProviders() {
        return new HashSet<>(providerRepository.findAll());
    }

    /* Private Methods */
}
