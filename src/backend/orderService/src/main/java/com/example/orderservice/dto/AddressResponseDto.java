package com.example.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddressResponseDto {

    @NotNull(message = "Address number field cannot be null and has to be an integer")
    private Integer number;
    @NotBlank(message = "Address street field cannot be null or blank")
    private String street;
    @NotBlank(message = "Address city field cannot be null or blank")
    private String city;
    @Nullable
    private String state;
    @NotBlank(message = "Address country field cannot be null or blank")
    private String country;
    @NotBlank(message = "Address zipcode field cannot be null or blank")
    private String zipcode;
    @Nullable
    private String info;
}
