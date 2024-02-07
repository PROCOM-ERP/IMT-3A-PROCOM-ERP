package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.AddressDto;
import com.example.inventoryservice.dto.CategoryDto;
import com.example.inventoryservice.model.*;
import com.example.inventoryservice.dto.ItemDto;
import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Optional<ProductDto> getProductById(int id){
        Optional<Product> productOptional = productRepository.findById(id);
        return productOptional.map(ProductService::productToDto);
    }

    public List<ProductDto> getAllProducts(){
        return productRepository.findAll()
                .stream()
                .map(ProductService::productToDto)
                .toList();
    }

    static ProductDto productToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .items(itemToDtoList(product.getItems()))
                .categories(categoryToDtoList(product.getCategories()))
                .build();
    }

    static ProductDto productItemToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .items(itemToDtoList(product.getItems()))
                .build();
    }

    static ProductDto productCategoryToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .categories(categoryToDtoList(product.getCategories()))
                .build();
    }

    static List<ItemDto> itemToDtoList(List<Item> items){
        return items.stream()
                .map(ItemService::itemAddressToDto)
                .toList();
    }

    static List<CategoryDto> categoryToDtoList(List<Category> category){
        return category.stream()
                .map(CategoryService::categoryOnlyToDto)
                .toList();
    }
}
