package com.example.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleUpdateRequestDto {

    @NotNull(message = "Role activation status cannot be null.")
    private Boolean isEnable;

    @NotNull(message = "Role permission set cannot be null, but can be empty.")
    private Set<@NotBlank(message = "Permission name cannot be null or blank.") String> permissions;

}
