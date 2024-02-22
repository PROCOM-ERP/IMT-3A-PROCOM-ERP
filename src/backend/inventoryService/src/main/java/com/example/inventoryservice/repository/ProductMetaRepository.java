package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.ProductMeta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMetaRepository extends JpaRepository<ProductMeta, Integer> {
}