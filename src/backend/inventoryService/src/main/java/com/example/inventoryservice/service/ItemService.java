package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.*;
import com.example.inventoryservice.dto.TransactionDto;
import com.example.inventoryservice.dtoRequest.MoveItemRequestDto;
import com.example.inventoryservice.dtoRequest.NewItemRequestDto;
import com.example.inventoryservice.dtoRequest.QuantityUpdateRequestDto;
import com.example.inventoryservice.model.Address;
import com.example.inventoryservice.model.Product;
import com.example.inventoryservice.model.Transaction;
import com.example.inventoryservice.model.Item;
import com.example.inventoryservice.repository.AddressRepository;
import com.example.inventoryservice.repository.ItemRepository;
import com.example.inventoryservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public void addNewItem(NewItemRequestDto newQuantity){
        Product product = productRepository.findById(newQuantity.getProductId()).orElseThrow();
        Address address = addressRepository.findById(newQuantity.getAddressId()).orElseThrow();

        Item item = Item.builder()
                .product(product)
                .address(address)
                .quantity(newQuantity.getQuantity())
                .build();

        Transaction transaction = Transaction.builder()
                .item(item)
                .employee(newQuantity.getEmployee())
                .timestamp(Instant.now())
                .quantity(newQuantity.getQuantity())
                .build();

        // Inject in the Item the previous transaction as a List of one element.
        item.setTransactions(Collections.singletonList(transaction));

        itemRepository.save(item);
    }

    @Transactional
    public void updateQuantity(QuantityUpdateRequestDto quantityUpdate){

        Item item = itemRepository.findById(quantityUpdate.getItemId()).orElse(null);
        // Employee employee = employee.findById(quantityUpdate.getEmployee()).orElse(null)
        if (item == null){
            throw new DataIntegrityViolationException("The item Id refers to a non existent item.");    // Error 422
        }
        else if(quantityUpdate.getQuantity() == 0){
            throw new DataIntegrityViolationException("The quantity is null.");                         // Error 422
        }
        else if (quantityUpdate.getEmployee() != null){ // <- replace with employee != null
            throw new DataIntegrityViolationException("The employee does not exists.");                 // Error 422
        }
        else if (quantityUpdate.getQuantity() + item.getQuantity() < 0){
            throw new IllegalArgumentException("The new quantity cannot be lower than 0.");             // Error 400
        }

        Transaction transaction = Transaction.builder()
                .quantity(quantityUpdate.getQuantity())
                .timestamp(Instant.now())
                .employee(quantityUpdate.getEmployee())
                .item(item)
                .build();
        item.setQuantity(item.getQuantity() + quantityUpdate.getQuantity());
        List<Transaction> transactionList = item.getTransactions();
        transactionList.add(transaction);
        item.setTransactions(transactionList);
    }

    public void moveToAddress(MoveItemRequestDto moveItemRequestDto){

    }

    static ItemDto itemAddressToDto(Item item){
        return ItemDto.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .transactions(transactionToDtoList(item.getTransactions()))
                .address(AddressService.addressOnlyToDto(item.getAddress()))
                .build();
    }

    static ItemDto itemProductToDto(Item item){
        return ItemDto.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .transactions(transactionToDtoList(item.getTransactions()))
                .product(ProductService.productNoCategoryToDto(item.getProduct()))
                .build();
    }

    static List<TransactionDto> transactionToDtoList(List<Transaction> transaction){
        return transaction.stream()
                .map(TransactionService::transactionToDto)
                .toList();
    }
}
