package com.example.inventoryservice.dtoRequest;

import jakarta.validation.Valid;
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
    @Pattern(regexp = "^[^';\"\\\\]*$", message = ("Bad request"))
    private String title;
    @Pattern(regexp = "^[^';\"\\\\]*$", message = ("Bad request"))
    private String description;
    @NotNull
    @NotEmpty
    private List<Integer> categories;   // This contains the id of each category.
    private List<ProductMetaRequestDto> productMeta;
    @Min(0)
    @NotNull
    private Integer numberOfItem;       // Defines the initial quantity of this product.
    @Min(1)
    private Integer address;    // Should be empty if numberOfItem == 0;
    @AssertTrue(message = "Address is required when numberOfItem is greater than 1")
    private boolean isAddressValid() {
        return numberOfItem == 0 || (numberOfItem >= 1 && address != null);
    }
}
