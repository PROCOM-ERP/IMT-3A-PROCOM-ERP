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
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final Logger logger = LoggerFactory.getLogger(InventoryServiceApplication.class);

    /**
     * Function that adds a new item in the database.
     * @param newQuantity is a DTO that includes:
     *                    Integer productId;    -> id of the managed product
     *                    Integer quantity;     -> (positive) quantity added to the item
     *                    String employee;      -> id of the employee/operator. ex: 'B00001'
     *                    Integer addressId;    -> id of the allocated address
     */
    @Transactional
    public void addNewItem(NewItemRequestDto newQuantity){

        logger.info("Start adding a new item...");
        Product product = productRepository.findById(newQuantity.getProductId()).orElseThrow(
                () -> new NoSuchElementException("The item id refers to a non-existent item."));       // E404
        Address address = addressRepository.findById(newQuantity.getAddressId()).orElseThrow(
                () -> new NoSuchElementException("The address id refers to a non-existent address.")); // E404
        // Employee employee = employeeRepository.findById(newQuantity.getEmployeeId()).orElseThrow(
        //                () -> new NoSuchElementException("The employee id refers to a non-existent employee."));

        if (newQuantity.getQuantity() <= 0){
            throw new IllegalArgumentException("The created item must be positive.");                  // E400
        }

        for (Item produtItem : product.getItems()){
            // If this true, then this mean the address already exists. So me must not create another item with
            // the same address:
            if(Objects.equals(produtItem.getAddress().getId(), address.getId())){
                QuantityUpdateRequestDto quantityUpdate = new QuantityUpdateRequestDto(
                        produtItem.getId(),
                        newQuantity.getQuantity(),
                        newQuantity.getEmployee());
                updateQuantity(quantityUpdate);     // This call the updateQuantity() function.
                logger.info("The new item refers to an already existing address.");
                // The new item refers to an already existing address.
                // So the item at this address is being updated instead.
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
        logger.info("New item correctly added correctly.");
    }

    /**
     * Function that update the quantity of the item.
     * @param quantityUpdate is a DTO that includes:
     *                       Integer itemId;    -> id of the updated item
     *                       Integer quantity;  -> (positive or negative) quantity added to the item
     *                       String employee;   -> id of the employee/operator. ex: 'B00001'
     */
    @Transactional
    public void updateQuantity(QuantityUpdateRequestDto quantityUpdate){

        logger.info("Start updating the item...");
        Item item = itemRepository.findById(quantityUpdate.getItemId()).orElseThrow(
                () -> new NoSuchElementException("The item id refers to a non existent item."));// Error 404
        // Employee employee = employeeRepository.findById(newQuantity.getEmployeeId()).orElseThrow(
        //                () -> new NoSuchElementException("The employee id refers to a non-existent employee."));

        if(quantityUpdate.getQuantity() == 0){
            throw new IllegalArgumentException("The quantity cannot be null.");                         // Error 400
        }
        else if (quantityUpdate.getQuantity() + item.getQuantity() < 0){
            throw new DataIntegrityViolationException("The new quantity cannot be lower than 0.");      // Error 422
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

        itemRepository.save(item);
    }

    /**
     * Function that moves the whole quantity of an item to another item.
     * @param moveItemRequest is a DTO that includes:
     *                           Integer itemId;    -> id of the source item
     *                           Integer addressId; -> id of the destination address
     *                           String employee;   -> id of the employee/operator. ex: 'B00001'
     */
    @Transactional
    public void moveToAddress(MoveItemRequestDto moveItemRequest){

        logger.info("Start changing item address...");
        Item item = itemRepository.findById(moveItemRequest.getItemId()).orElseThrow(
                () -> new NoSuchElementException("The item id refers to a non existent item."));       // E404
        Address address = addressRepository.findById(moveItemRequest.getAddressId()).orElseThrow(
                () -> new NoSuchElementException("The address id refers to a non existent address.")); // E404
        // Employee employee = employeeRepository.findById(newQuantity.getEmployeeId()).orElseThrow(
        //                () -> new NoSuchElementException("The employee Id refers to a non-existent employee."));

        if (Objects.equals(item.getAddress().getId(), moveItemRequest.getAddressId())){
            throw new DataIntegrityViolationException("The pointed address is the same as before.");   // E422
        }
        else if(item.getQuantity() <= 0){
            throw new DataIntegrityViolationException("The pointed item is is empty. Cannot be moved.");
        }

        List<Item> neighborItems = item.getProduct().getItems();
        for (Item neighborItem: neighborItems){
            // This is true if another item of the same product has the address destination as address.
            if(Objects.equals(neighborItem.getAddress().getId(), moveItemRequest.getAddressId())){
                // If true, we just update the quantity of the both items.

                // Cleans the previous Item:
                QuantityUpdateRequestDto quantityCleanRequestDto = new QuantityUpdateRequestDto(
                        moveItemRequest.getItemId(),
                        -item.getQuantity(),
                        moveItemRequest.getEmployee()
                );
                // Transfers the quantity to the destination Item.
                QuantityUpdateRequestDto quantityTransferredRequestDto = new QuantityUpdateRequestDto(
                        neighborItem.getId(),
                        item.getQuantity(),
                        moveItemRequest.getEmployee()
                );
                updateQuantity(quantityCleanRequestDto);
                updateQuantity(quantityTransferredRequestDto);
                return;
            }
        }

        // No other item belonging to the same product as the destination address.
        //Creates the new Item:
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto(
                item.getProduct().getId(),
                item.getQuantity(),
                moveItemRequest.getEmployee(),
                moveItemRequest.getAddressId());

        // Cleans the previous Item:
        QuantityUpdateRequestDto quantityCleanRequestDto = new QuantityUpdateRequestDto(
                moveItemRequest.getItemId(),
                -item.getQuantity(),
                moveItemRequest.getEmployee()
        );
        addNewItem(newItemRequestDto);
        updateQuantity(quantityCleanRequestDto);
    }

    // DTO converters:

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
