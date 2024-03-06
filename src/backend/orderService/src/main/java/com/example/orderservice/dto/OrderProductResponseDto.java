package com.example.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderProductResponseDto {

    @NotBlank(message = "Order product reference cannot be null or blank")
    private String reference;
    @NotNull(message = "Order product unit price cannot be null or 0")
    private BigDecimal unitPrice;
    @NotNull(message = "Order product quantity cannot be null or 0")
    private Integer quantity;

}
