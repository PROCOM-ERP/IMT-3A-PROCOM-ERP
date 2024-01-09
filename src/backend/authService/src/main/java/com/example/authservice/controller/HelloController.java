package com.example.authservice.controller;

import com.example.authservice.model.Path;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(Path.V1_HELLO)
@RequiredArgsConstructor
public class HelloController {

    @Value("${security.services.sharedkey}") private String sharedKey;

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping
    @Operation(operationId = "getHello", tags = {"hello"},
            summary = "GET Hello World !", description =
            "GET Hello World ! from AuthService")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Hello World message got correctly",
                    content = {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = String.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> getHello() {
        String directoryServiceUrl = "http://springboot-procom-erp-dir-service:8042/api/v1/hello";

        // Création des en-têtes HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", sharedKey);
        logger.info("Authorization : " + headers.get("Authorization"));

        // Création de l'entité HTTP avec les en-têtes
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Envoi de la requête avec RestTemplate
        ResponseEntity<String> response = restTemplate.exchange(
                directoryServiceUrl, HttpMethod.GET, entity, String.class);

        return ResponseEntity.ok("Hello, World ! Response from Directory Service: " + response.getBody());
    }


}
