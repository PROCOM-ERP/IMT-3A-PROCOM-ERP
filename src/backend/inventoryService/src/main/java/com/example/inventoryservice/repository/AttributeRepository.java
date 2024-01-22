package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeRepository extends JpaRepository<Attribute, Integer> {
}