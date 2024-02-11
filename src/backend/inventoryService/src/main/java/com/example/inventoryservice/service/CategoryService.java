package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.AddressDto;
import com.example.inventoryservice.dto.CategoryDto;
import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.model.Address;
import com.example.inventoryservice.model.Category;
import com.example.inventoryservice.model.Product;
import com.example.inventoryservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    final private CategoryRepository categoryRepository;
    public Optional<CategoryDto> getCategoryById(int id){
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        return categoryOptional.map(CategoryService::categoryToDto);
    }

    public List<CategoryDto> getAllCategories(){
        logger.info("Hello !");
        return categoryRepository.findAll()
                .stream()
                .map(CategoryService::categoryOnlyToDto)
                .toList();
    }

    static CategoryDto categoryOnlyToDto(Category category){
        return CategoryDto.builder()
                .id(category.getId())
                .title(category.getTitle())
                .description(category.getDescription())
                .build();
    }

    static CategoryDto categoryToDto(Category category){
        return CategoryDto.builder()
                .id(category.getId())
                .title(category.getTitle())
                .description(category.getDescription())
                .products(productItemToDtoList(category.getProducts()))
                .build();
    }

    static List<ProductDto> productItemToDtoList(List<Product> products) {
        return products.stream()
                .map(ProductService::productItemToDto)
                .toList();
    }
}
