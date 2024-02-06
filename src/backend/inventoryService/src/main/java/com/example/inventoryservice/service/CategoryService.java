package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.CategoryDto;
import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.model.Category;
import com.example.inventoryservice.model.Product;
import com.example.inventoryservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    final private CategoryRepository categoryRepository;
    public List<CategoryDto> getCategoryById(int id){
        List<CategoryDto> categoryInList = new ArrayList<>();
        categoryInList.add(categoryRepository.findById(id)
                .map(CategoryService::categoryToDto)
                .orElseThrow());
        return categoryInList;
    }

    public List<CategoryDto> getAllCategories(){
        return categoryRepository.findAll()
                .stream()
                .map(CategoryService::categoryToDto)
                .toList();
    }

    static CategoryDto categoryOnlyToDto(Category category){
        return CategoryDto.builder()
                .title(category.getTitle())
                .description(category.getDescription())
                .build();
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
}
