package com.example.directoryservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.lang.Nullable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AddressCreationRequestDto {

    @NotNull(message = "Address number field cannot be null and has to be an integer.")
    private Integer number;
    @NotBlank(message = "Address street field cannot be null or blank.")
    @Size(message = "Address street field must contain between 1 and 255 characters.")
    private String street;
    @NotBlank(message = "Address city field cannot be null or blank.")
    @Size(message = "Address city field must contain between 1 and 100 characters.")
    private String city;
    @Nullable
    private String state;
    @NotBlank(message = "Address country field cannot be null or blank.")
    @Size(message = "Address country field must contain between 1 and 100 characters.")
    private String country;
    @NotBlank(message = "Address zipcode field cannot be null or blank.")
    @Size(message = "Address zipcode field must contain between 1 and 20 characters.")
    private String zipcode;
    @Nullable
    private String info;

}
