package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.ProductDto;
import com.example.inventoryservice.model.Product;
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

    static ProductDto productToDto(Product product) {
        return ProductDto.builder()
                .title(product.getTitle())
                .description(product.getDescription())
                .build();
    }
}
