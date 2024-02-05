package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.ItemDto;
import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.dto.TransactionDto;
import com.example.inventoryservice.model.Item;
import com.example.inventoryservice.model.Product;
import com.example.inventoryservice.model.Transaction;
import com.example.inventoryservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    static ItemDto itemToDto(Item item){
        return ItemDto.builder()
                .quantity(item.getQuantity())
                .transactions(item.getTransactions())
                .build();
    }
}
