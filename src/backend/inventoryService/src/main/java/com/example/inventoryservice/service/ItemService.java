package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.*;
import com.example.inventoryservice.dto.TransactionDto;
import com.example.inventoryservice.model.Category;
import com.example.inventoryservice.model.Transaction;
import com.example.inventoryservice.model.Item;
import com.example.inventoryservice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    static ItemDto itemAddressToDto(Item item){
        return ItemDto.builder()
                .quantity(item.getQuantity())
                .transactions(transactionToDtoList(item.getTransactions()))
                .address(AddressService.addressToDto(item.getAddress()))
                .build();
    }

    static ItemDto itemProductToDto(Item item){
        return ItemDto.builder()
                .quantity(item.getQuantity())
                .transactions(transactionToDtoList(item.getTransactions()))
                .product(ProductService.productCategoryToDto(item.getProduct()))
                .build();
    }

    static List<TransactionDto> transactionToDtoList(List<Transaction> transaction){
        return transaction.stream()
                .map(TransactionService::transactionToDto)
                .toList();
    }
}
