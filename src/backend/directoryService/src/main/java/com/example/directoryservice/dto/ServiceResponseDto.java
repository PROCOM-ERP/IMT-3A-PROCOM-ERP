package com.example.directoryservice.dto;

import com.example.directoryservice.model.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResponseDto {

    private Integer id;
    private String name;
    private Address address;
    private String organisation;
    private Set<String> employees;

}
