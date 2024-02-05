package com.example.inventoryservice.dto;

import com.example.inventoryservice.model.Transaction;
import com.example.inventoryservice.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    private Integer quantity;
    private List<Transaction> transactions;
    private Address address;
}
