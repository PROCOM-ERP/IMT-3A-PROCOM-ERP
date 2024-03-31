package com.example.orderservice.controller;

import com.example.orderservice.dto.HttpStatusErrorDto;
import com.example.orderservice.model.Path;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Path.V1_HELLO)
@RequiredArgsConstructor
public class HelloController {

    /* Endpoints Methods */

    @GetMapping(produces = "application/json")
    @Operation(operationId = "getHello", tags = {"hello"},
            summary = "GET Hello World !", description =
            "GET Hello World ! from Order Service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Hello World message retrieved correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<String> getHello()
    {
        return ResponseEntity.ok("Hello World from OrderService!");
    }
}
