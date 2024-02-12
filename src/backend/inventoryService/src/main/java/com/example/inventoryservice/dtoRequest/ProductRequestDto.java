package com.example.inventoryservice.dtoRequest;

import com.example.inventoryservice.dto.CategoryDto;
import com.example.inventoryservice.dto.ItemDto;
import com.example.inventoryservice.dto.ProductMetaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {
    private String title;
    private String description;
    private Integer numberOfItem;     // Defines the initial quantity of this product.
    private List<CategoryRequestDto> categories;
    private List<ProductMetaRequestDto> productMeta;
}
