package com.example.directoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class HttpStatusErrorDto {

    @NonNull
    private String message;

    @NonNull
    @Builder.Default
    private Map<String, String> fields = new HashMap<>();

}
