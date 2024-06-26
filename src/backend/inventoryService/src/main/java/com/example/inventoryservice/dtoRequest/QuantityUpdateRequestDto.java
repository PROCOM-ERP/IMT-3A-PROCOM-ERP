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
public class QuantityUpdateRequestDto {
    @Min(0)
    @NotNull
    private Integer itemId;
    @NotNull
    private Integer quantity;
  
    @NotBlank
    @NotNull
    @Pattern(regexp = "[A-Z][0-9]{5}")
    private String employee;
}
