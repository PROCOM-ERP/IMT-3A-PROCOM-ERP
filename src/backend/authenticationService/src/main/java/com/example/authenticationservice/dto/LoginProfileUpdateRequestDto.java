package com.example.authenticationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LoginProfileUpdateRequestDto {

    private Boolean isEnable;
    private Set<String> roles;

}
