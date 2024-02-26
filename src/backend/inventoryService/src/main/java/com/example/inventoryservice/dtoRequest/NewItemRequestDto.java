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
    @NotBlank
    @NotNull
    @Size(max=6)
    @Pattern(regexp = "[A-Z][0-9]{5}")
    private String employee;
    @Min(0)
    @NotNull
    private Integer addressId;
}
