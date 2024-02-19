package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.CategoryDto;
import com.example.inventoryservice.model.Category;
import com.example.inventoryservice.service.CategoryService;
import com.example.inventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost")
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Integer id){
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategory(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}
