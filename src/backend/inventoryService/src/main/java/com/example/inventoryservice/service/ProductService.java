package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.*;
import com.example.inventoryservice.model.*;
import com.example.inventoryservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductDto> getProductById(int id){
        List<ProductDto> productInList = new ArrayList<>();
        productInList.add(productRepository.findById(id)
                .map(ProductService::productToDto)
                .orElseThrow());
        return productInList;
    }

    public List<ProductDto> getAllProducts(){
        return productRepository.findAll()
                .stream()
                .map(ProductService::productToDto)
                .toList();
    }

    static CategoryDto categoryToDto(Category category){
        return CategoryDto.builder()
                .title(category.getTitle())
                .description(category.getDescription())
                .products(productToDtoList(category.getProducts()))
                .build();
    }

    static List<ProductDto> productToDtoList(List<Product> products) {
        return products.stream()
            .map(ProductService::productToDto)
            .toList();
    }


    static ProductDto productToDto(Product product) {
        return ProductDto.builder()
                .title(product.getTitle())
                .description(product.getDescription())
                .items(itemToDtoList(product.getItems()))
                .build();
    }

    static List<ItemDto> itemToDtoList(List<Item> items){
        return items.stream()
                .map(ItemService::itemToDto)
                .toList();
    }

    static ItemDto itemToDto(Item item){
        return ItemDto.builder()
                .quantity(item.getQuantity())
                .transaction(item.).build();
    }

    static AddressDto addressToDto(Address address){

    }

    static TransactionDto transactionToDto(Transaction transaction){

    }
}
