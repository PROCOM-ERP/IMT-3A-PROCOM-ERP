package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderProductCreationRequestDto {

    private String reference;
    private BigDecimal unitPrice;
    private Integer quantity;

}
