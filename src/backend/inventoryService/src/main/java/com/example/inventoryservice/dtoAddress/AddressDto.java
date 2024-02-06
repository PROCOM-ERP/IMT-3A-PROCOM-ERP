package com.example.inventoryservice.dtoAddress;

import com.example.inventoryservice.dtoProduct.ItemDto;

import java.util.List;

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
