package com.example.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeCreationRequestDto {

    @NotBlank
    private String id;
    @NotBlank
    private String lastName;
    @NotBlank
    private String firstName;
    @NotBlank
    private String email;
    @NotBlank
    private String phoneNumber;

}
