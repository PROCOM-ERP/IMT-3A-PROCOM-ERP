package com.example.inventoryservice.dto;

import java.util.List;

public class FullAddressDto {
    private String number;
    private String street;
    private String city;
    private String state;
    private String country; // Comme les kinder.
    private String postal_code;
    private String info;

    // Add information for reverse searching: From Address to Items, products, category.
}
