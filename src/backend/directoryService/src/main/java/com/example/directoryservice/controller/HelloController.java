package com.example.directoryservice.controller;

import com.example.directoryservice.model.Path;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Path.V1_HELLO)
@RequiredArgsConstructor
public class HelloController {

    private final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping
    @Operation(operationId = "getHello", tags = {"hello"},
            summary = "GET Hello World !", description =
            "GET Hello World ! from HelloService")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Hello World message got correctly",
                    content = {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = String.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> getHello() {
        logger.info("Arrived to endpoint");
        return ResponseEntity.ok("Hello World from DirectoryService!");
    }

}