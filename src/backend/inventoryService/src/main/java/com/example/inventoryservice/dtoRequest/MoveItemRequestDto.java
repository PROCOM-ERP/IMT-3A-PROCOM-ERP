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
    private Integer itemId;
    @Min(0)
    private Integer addressId;
    @NotBlank
    @NotNull
    @Size(max=6)
    @Pattern(regexp = "[A-Z][0-9]{5}")
    private String employee;
}
