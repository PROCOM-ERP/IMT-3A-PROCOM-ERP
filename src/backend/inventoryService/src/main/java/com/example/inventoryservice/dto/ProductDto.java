package com.example.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Integer id;
    private String title;
    private String description;
    private List<ItemDto> items;
    private List<CategoryDto> categories;
    private List<ProductMetaDto> productMeta;
    private Integer quantity;
}
