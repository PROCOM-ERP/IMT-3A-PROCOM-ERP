package com.example.gatewayService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@AllArgsConstructor
@Getter
public enum Endpoint {

    POST_V1_MAILS(HttpMethod.POST,
            Path.V1_MAILS,
            Permission.CanCreateMail);

    private final HttpMethod httpMethod;
    private final String path;
    private final Permission permission;
}
