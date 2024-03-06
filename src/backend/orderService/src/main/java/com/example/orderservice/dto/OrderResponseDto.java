package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderResponseDto {

    private Integer id;
    private Date createdAt;
    private String provider;
    private BigDecimal totalAmount;
    private String orderer;
    private String approver;
    private Set<StatusResponseDto> status;
    private Set<OrderProductResponseDto> products;

}
