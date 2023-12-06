package com.example.authservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
@AllArgsConstructor
public class UserRequestDto {

    private String role;
    private String password;

}
