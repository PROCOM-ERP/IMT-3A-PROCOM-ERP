package com.example.directoryservice.dto;

import lombok.Getter;

import java.util.Set;

@Getter
public class RoleRequestDto {

    private String name;
    private Set<String> permissions;

}
