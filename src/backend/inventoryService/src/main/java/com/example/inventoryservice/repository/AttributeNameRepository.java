package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.AttributeName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeNameRepository extends JpaRepository<AttributeName, Integer> {
}