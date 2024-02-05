package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost")
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory/product/")
public class ProductController {

    @GetMapping("/{id}")
    public ResponseEntity<List<ProductDto>> getProductById(@PathVariable int id){
        return ResponseEntity.ok(ProductService.getProductById());
    }
}
