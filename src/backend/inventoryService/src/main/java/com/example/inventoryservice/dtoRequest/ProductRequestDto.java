package com.example.inventoryservice.dtoRequest;

import com.example.inventoryservice.dto.AddressDto;
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
    private List<Integer> categories;   // This contains the id of each category.
    private List<ProductMetaRequestDto> productMeta;
    private Integer numberOfItem;       // Defines the initial quantity of this product.
    private Integer address;    // Should be empty if numberOfItem == 0;
}
