package com.example.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {
    private Integer id;
    private String number;
    private String street;
    private String city;
    private String state;
    private String country; // Comme les kinder.
    private String postal_code;
    private String info;
    private List<ItemDto> items;
}
