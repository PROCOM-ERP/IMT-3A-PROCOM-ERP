package com.example.inventoryservice.dtoRequest;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

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
    @Pattern(regexp = "^[^';\"\\\\]*$")
    private List<Integer> categories;   // This contains the id of each category.
    @Pattern(regexp = "^[^';\"\\\\]*$")
    private List<ProductMetaRequestDto> productMeta;
    @Min(0)
    @NotNull
    @Pattern(regexp = "^[^';\"\\\\]*$")
    private Integer numberOfItem;       // Defines the initial quantity of this product.
    @Min(0)
    @Pattern(regexp = "^[^';\"\\\\]*$")
    private Integer address;    // Should be empty if numberOfItem == 0;
}
