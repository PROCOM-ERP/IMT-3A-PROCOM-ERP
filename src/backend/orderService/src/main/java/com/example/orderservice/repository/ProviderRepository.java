package com.example.orderservice.repository;

import com.example.orderservice.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProviderRepository extends JpaRepository<Provider, Integer>, JpaSpecificationExecutor<Provider> {
}