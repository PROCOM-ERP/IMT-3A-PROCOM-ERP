package com.example.inventoryservice.dto;

import com.example.inventoryservice.model.Product;
import com.example.inventoryservice.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Integer quantity;
    private TransactionDto transaction;
}
