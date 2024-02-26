package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.CategoryDto;
import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.dtoRequest.CategoryRequestDto;
import com.example.inventoryservice.model.Category;
import com.example.inventoryservice.model.Product;
import com.example.inventoryservice.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    final private CategoryRepository categoryRepository;
    public CategoryDto getCategoryById(Integer id)
            throws NoSuchElementException {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        return categoryOptional.map(CategoryService::categoryToDto).orElseThrow();  // E404
    }

    public List<CategoryDto> getAllCategories(){
        //logger.info("Hello !");
        return categoryRepository.findAll()
                .stream()
                .map(CategoryService::categoryOnlyToDto)
                .toList();
    }

    public List<Category> getAllByIds(List<Integer> ids){
        return categoryRepository.findByIds(ids);
    }

    @Transactional
    public void createCategory(CategoryRequestDto categoryRequest){

        List<Category> categories = categoryRepository.findByTitle(categoryRequest.getTitle());
        if (categories != null && !categories.isEmpty()){   // Error 422
            logger.error("The requested category already exists.");
            throw new DataIntegrityViolationException("The requested category already exists.");
        }
        if (Objects.equals(categoryRequest.getTitle(), "")){              // Error 422
            logger.error("Title is empty.");
            throw new DataIntegrityViolationException("Title is empty.");
        }

        Category category = Category.builder()
                .title(categoryRequest.getTitle())
                .description(categoryRequest.getDescription())
                .build();

        categoryRepository.save(category);

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
