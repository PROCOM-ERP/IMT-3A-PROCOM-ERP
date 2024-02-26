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
public class ProductMetaRequestDto {
    @NotBlank
    @NotNull
    @Size(max=255)
    @Pattern(regexp = "^[^';\"\\\\]*$")
    private String key;
    @NotBlank
    @NotNull
    @Pattern(regexp = "^[^';\"\\\\]*$")
    private String value;
    @NotBlank
    @NotNull
    @Size(max=15)
    @Pattern(regexp = "^[^';\"\\\\]*$")
    private String type;
    @Pattern(regexp = "^[^';\"\\\\]*$")
    private String description;
}
