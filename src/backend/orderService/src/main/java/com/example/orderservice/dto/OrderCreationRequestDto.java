package com.example.orderservice.dto;

import com.example.orderservice.utils.CustomStringUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class  OrderCreationRequestDto {

    @NotNull(message = "Order provider cannot be null and has to be an integer.")
    private Integer provider;

    @NotBlank(message = "Order quote cannot be null or blank.")
    @Size(min = 1, max = 64, message = "Order quote must contain between 1 and 64 characters.")
    @Pattern(regexp = CustomStringUtils.REGEX_ORDER_QUOTE,
            message = "Order quote must start with a letter or number, and can only include letters, " +
                    "numbers, underscores (_), hyphens (-), and spaces. " +
                    "Consecutive special characters or spaces are not allowed.")
    private String quote;

    @NotNull(message = "Order address cannot be null.")
    @Valid
    private AddressCreationRequestDto address;

    @NotNull(message = "Order orderer information cannot be null.")
    @Valid
    private EmployeeCreationRequestDto orderer;

    @NotNull(message = "Order product list cannot be null.")
    @Size(min = 1, message = "Order product list must have at least 1 product.")
    private Set<@NotNull(message = "An order product cannot be null.") @Valid
            OrderProductCreationRequestDto> products;

}
