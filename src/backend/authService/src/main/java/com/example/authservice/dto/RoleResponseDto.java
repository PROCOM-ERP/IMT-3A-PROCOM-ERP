package com.example.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponseDto {

    private String name;
    private Boolean enable;
    private Set<String> permissions;
    private Set<String> employees;

}
