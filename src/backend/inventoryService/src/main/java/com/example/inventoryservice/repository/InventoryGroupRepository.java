package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.InventoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryGroupRepository extends JpaRepository<InventoryGroup, Integer> {
}