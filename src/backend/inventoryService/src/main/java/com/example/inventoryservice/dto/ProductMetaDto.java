package com.example.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMetaDto {
    private Integer id;
    private String key;
    private String value;
    private String type;
    private String description;
    private ProductDto product;
}
