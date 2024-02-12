package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.ProductMetaDto;
import com.example.inventoryservice.dto.TransactionDto;
import com.example.inventoryservice.model.ProductMeta;
import com.example.inventoryservice.model.Transaction;
import com.example.inventoryservice.repository.ProductMetaRepository;
import com.example.inventoryservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductMetaService {
    final private ProductMetaRepository productMetaRepository;

    static ProductMetaDto productMetaToDto(ProductMeta productMeta){
        return ProductMetaDto.builder()
                .id(productMeta.getId())
                .key(productMeta.getKey())
                .value(productMeta.getValue())
                .type(productMeta.getType())
                .description(productMeta.getDescription())
                .build();
    }
}