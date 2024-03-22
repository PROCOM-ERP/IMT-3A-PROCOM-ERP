package com.example.inventoryservice.dtoRequest;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewItemRequestDto {
    @Min(0)
    @NotNull
    private Integer productId;
    @Min(1)
    @NotNull
    private Integer quantity;
    @Min(0)
    @NotNull
    private Integer addressId;
}
