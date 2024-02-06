package com.example.authenticationservice.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LoginProfileMessageDto {

    private String id;
    private LocalDate createdAt;
    private Boolean isEnable;
    private String lastName;
    private String firstName;
    private String email;
    private String phoneNumber;
    private Integer service;

}
