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
public class MoveItemRequestDto {
    @Min(0)
    @NotNull
    private Integer itemId;
    @Min(0)
    @NotNull
    private Integer addressId;
}
