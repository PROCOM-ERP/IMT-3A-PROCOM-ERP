package com.example.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderCreationRequestDto {

    @NotNull(message = "Order provider cannot be null and has to be an integer")
    private Integer provider;
    @NotBlank(message = "Order quote cannot be null or blank")
    private String quote;
    @NotNull(message = "Order address cannot be null")
    private AddressCreationRequestDto address;
    @NotNull(message = "Order orderer cannot be null")
    private EmployeeCreationRequestDto orderer;
    @NotNull(message = "Order products cannot be null")
    @Size(min = 1, message = "Order product list must have at least 1 product")
    private Set<OrderProductCreationRequestDto> products;

}
