package com.example.inventoryservice.service;

import com.example.inventoryservice.InventoryServiceApplication;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final Logger logger = LoggerFactory.getLogger(InventoryServiceApplication.class);

    /**
     * Function that adds a new item in the database.
     * @param newQuantity = Integer productId; Integer quantity; String employee; Integer addressId;
     */
    @Transactional
    public void addNewItem(NewItemRequestDto newQuantity){

        logger.info("Start adding a new item...");
        Product product = productRepository.findById(newQuantity.getProductId()).orElseThrow(
                () -> new DataIntegrityViolationException("The item Id refers to a non existent item."));
        Address address = addressRepository.findById(newQuantity.getAddressId()).orElseThrow(
                () -> new DataIntegrityViolationException("The address Id refers to a non existent address."));
        // Employee employee = employeeRepository.findById(newQuantity.getEmployeeId()).orElseThrow(
        //                () -> new DataIntegrityViolationException("The employee Id refers to a non existent employee."));

        if (newQuantity.getQuantity() <= 0){
            throw new DataIntegrityViolationException("The created item must be positive.");    // Error 422
        }

        for (Item produtItem : product.getItems()){
            // If this true, then this mean the address already exists. So me must not create another item with
            // the same address:
            if(Objects.equals(produtItem.getAddress().getId(), address.getId())){
                QuantityUpdateRequestDto quantityUpdate = new QuantityUpdateRequestDto(
                        produtItem.getId(),
                        newQuantity.getQuantity(),
                        newQuantity.getEmployee());
                updateQuantity(quantityUpdate);
                logger.warn("The client as tried to create another existing item. The item has been updated through.");
                return;
            }
        }

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

        logger.info("Start updating the item...");
        Item item = itemRepository.findById(quantityUpdate.getItemId()).orElseThrow(
                () -> new DataIntegrityViolationException("The item Id refers to a non existent item."));
        // Employee employee = employeeRepository.findById(newQuantity.getEmployeeId()).orElseThrow(
        //                () -> new DataIntegrityViolationException("The employee Id refers to a non existent employee."));

        if(quantityUpdate.getQuantity() == 0){
            throw new DataIntegrityViolationException("The quantity is null.");                         // Error 422
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

        logger.info("Start changing item address...");
        Item item = itemRepository.findById(moveItemRequestDto.getItemId()).orElseThrow(
                () -> new DataIntegrityViolationException("The item Id refers to a non existent item."));
        Address address = addressRepository.findById(moveItemRequestDto.getAddressId()).orElseThrow(
                () -> new DataIntegrityViolationException("The address Id refers to a non existent address."));
        // Employee employee = employeeRepository.findById(newQuantity.getEmployeeId()).orElseThrow(
        //                () -> new DataIntegrityViolationException("The employee Id refers to a non existent employee."));

        List<Integer> itemIdList = new ArrayList<>();
        for (Item ExistingItem: address.getItems())
            itemIdList.add(ExistingItem.getId());
        if (itemIdList.contains(item.getId()){
            //
        }
        else{

        }
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
