package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddressCreationRequestDto {

    private Integer number;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipcode;
    private String info;

}
