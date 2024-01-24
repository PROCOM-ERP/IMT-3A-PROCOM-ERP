package com.example.directoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponseDto {

    private Integer id;
    private Integer number;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String info;
    private String organisation;
    private Set<Integer> services;

}
