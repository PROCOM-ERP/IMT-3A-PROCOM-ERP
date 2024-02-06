package com.example.inventoryservice.service;

import com.example.inventoryservice.dtoProduct.CategoryDto;
import com.example.inventoryservice.dtoProduct.ProductDto;
import com.example.inventoryservice.model.Category;
import com.example.inventoryservice.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {


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
}
