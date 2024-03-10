package com.example.authenticationservice.dto;

import com.example.authenticationservice.utils.CustomStringUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleCreationRequestDto {

    @NotBlank(message = "Role cannot be null or blank.")
    @Size(min = 1, max = 32, message = "Role name must contain between 1 and 32 characters.")
    @Pattern(regexp = CustomStringUtils.REGEX_ROLE_NAME,
            message = "Role name must start with a letter and can only contain letters, numbers, dashes, and dots. " +
                    "Consecutive special characters are not allowed.")
    private String name;

    @NotNull(message = "Role microservice set cannot be null, but can be empty.")
    private Set<
            @NotBlank(message = "Microservice name cannot be null or blank.")
            @Size(min = 1, max = 32, message = "Microservice name must contain between 1 and 32 characters.")
            String> microservices;

}
