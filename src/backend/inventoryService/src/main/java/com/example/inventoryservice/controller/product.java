package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.ProductListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost")
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class product {

    @RequestMapping("{id}")
    public ProductListDto getProductById(@PathVariable int id){

    }
}
