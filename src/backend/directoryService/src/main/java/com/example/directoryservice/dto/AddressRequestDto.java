package com.example.directoryservice.dto;

import lombok.Getter;

@Getter
public class AddressRequestDto {

    private Integer number;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String info;

}
