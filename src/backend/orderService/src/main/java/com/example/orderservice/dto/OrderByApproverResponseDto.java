package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderByApproverResponseDto {

    private Integer id;
    private Date createdAt;
    private String provider;
    private BigDecimal totalAmount;
    private String orderer;
    private String status;

}
