package com.example.orderservice.dto;

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

    private Integer provider;
    private String quote;
    private AddressCreationRequestDto address;
    private EmployeeCreationRequestDto employee;
    private Set<OrderProductCreationRequestDto> products;

}
