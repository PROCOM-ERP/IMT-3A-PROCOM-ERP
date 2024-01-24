package com.example.authenticationservice.dto;

import lombok.Getter;

import java.util.Set;

@Getter
public class RoleResponseAQMPDto {

    private String name;
    private Boolean enable;
    private Set<String> permissions;

}
