package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.model.Product;
import com.example.inventoryservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductDto getProductById(int id){
        return productRepository.findById(id)
                .map(ProductService::productToDto)
                .orElseThrow();
    }

    static ProductDto productToDto(Product product) {
        return ProductDto.builder()
                .title(product.getTitle())
                .description(product.getDescription())

                .build();
    }
}
