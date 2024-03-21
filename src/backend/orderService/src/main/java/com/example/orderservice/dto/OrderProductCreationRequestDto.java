package com.example.orderservice.dto;

import com.example.orderservice.utils.CustomStringUtils;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Order product reference cannot be null or blank.")
    @Size(min = 1, max = 128, message = "Order product reference must contain between 1 and 128 characters.")
    @Pattern(regexp = CustomStringUtils.REGEX_ORDER_PRODUCT_REFERENCE,
            message = "Order product reference must start with a letter or number and can include letters, " +
                    "numbers, underscores (_), hyphens (-), or spaces. " +
                    "However, each underscore, hyphen, or space must be followed by a letter or number, " +
                    "and cannot occur consecutively.")
    private String reference;

    @NotNull(message = "Order product unit price cannot be null or 0.")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "Order product unit price must be greater than 0.")
    private BigDecimal unitPrice;

    @NotNull(message = "Order product quantity cannot be null or 0.")
    @Min(value = 1, message = "Order product quantity must be greater than 0.")
    private Integer quantity;

}
