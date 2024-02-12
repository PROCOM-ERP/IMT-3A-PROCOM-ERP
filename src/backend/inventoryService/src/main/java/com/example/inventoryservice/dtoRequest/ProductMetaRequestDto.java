package com.example.inventoryservice.dtoRequest;

import com.example.inventoryservice.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMetaRequestDto {
    private String key;
    private String value;
    private String type;
    private String description;
}
