package com.example.directoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDto {

    private Integer number;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String info;

}
