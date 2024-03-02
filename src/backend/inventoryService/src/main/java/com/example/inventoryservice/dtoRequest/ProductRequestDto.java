package com.example.inventoryservice.dtoRequest;

import jakarta.validation.constraints.*;
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
    @NotBlank
    @NotNull
    @Size(max=127)
    @Pattern(regexp = "^[^';\"\\\\]*$")
    private String title;
    @Pattern(regexp = "^[^';\"\\\\]*$")
    private String description;
    private List<Integer> categories;   // This contains the id of each category.
    private List<ProductMetaRequestDto> productMeta;
    @Min(0)
    @NotNull
    private Integer numberOfItem;       // Defines the initial quantity of this product.
    @Min(0)
    private Integer address;    // Should be empty if numberOfItem == 0;
}
