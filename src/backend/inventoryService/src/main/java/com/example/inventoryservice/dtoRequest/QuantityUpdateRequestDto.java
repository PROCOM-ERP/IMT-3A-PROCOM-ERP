package com.example.inventoryservice.dtoRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuantityUpdateRequestDto {
    private Integer itemId;
    private Integer quantity;
    private String employee;
}
