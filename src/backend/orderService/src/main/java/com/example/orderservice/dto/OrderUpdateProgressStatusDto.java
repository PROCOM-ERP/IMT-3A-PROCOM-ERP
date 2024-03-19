package com.example.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OrderUpdateProgressStatusDto {

    @NotNull(message = "Order progress status cannot be null or 0.")
    private Integer idProgressStatus;
}
