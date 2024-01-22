package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByInventoryId(int inventoryId);
}