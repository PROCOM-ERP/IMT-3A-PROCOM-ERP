package com.example.inventoryservice.service;

import com.example.inventoryservice.dtoProduct.ItemDto;
import com.example.inventoryservice.model.Item;
import com.example.inventoryservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
