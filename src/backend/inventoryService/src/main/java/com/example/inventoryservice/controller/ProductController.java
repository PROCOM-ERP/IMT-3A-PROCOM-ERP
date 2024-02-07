package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost")
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory/product/")
public class ProductController {

    private final ProductService productService;
    @GetMapping("/{id}")
    public ResponseEntity<Optional<ProductDto>> getProductById(@PathVariable int id){
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }
}
