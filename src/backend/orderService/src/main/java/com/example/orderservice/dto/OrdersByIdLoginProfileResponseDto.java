package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdersByIdLoginProfileResponseDto {

    private Set<OrderByOrdererResponseDto> ordersByOrderer;
    private Set<OrderByApproverResponseDto> ordersByApprover;

}
