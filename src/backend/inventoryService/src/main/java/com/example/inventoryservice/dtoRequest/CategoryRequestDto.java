package com.example.inventoryservice.dtoRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDto {
    @NotBlank
    @NotNull
    @Size(max=127)
    @Pattern(regexp = "^[^';\"\\\\]*$")
    private String title;
    @Pattern(regexp = "^[^';\"\\\\]*$")
    private String description;
}
