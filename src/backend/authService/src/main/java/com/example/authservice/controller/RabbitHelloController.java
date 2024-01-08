package com.example.authservice.controller;

import com.example.authservice.model.Path;
import com.example.authservice.service.RabbitMQService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Path.V1_RABBITHELLO)
public class RabbitHelloController {

  private final RabbitMQService rabbitMQService;

  @Autowired
  public RabbitHelloController(RabbitMQService rabbitMQService) {
    this.rabbitMQService = rabbitMQService;
  }

  @GetMapping
  @Operation(operationId = "getHello", tags = {"hello"},
             summary = "GET Hello World !",
             description = "GET Hello World ! from AuthService and RabbitMQ")
  @ApiResponses(
      value =
      {
        @ApiResponse(responseCode = "200",
                     description = "Hello World message got correctly",
                     content =
                     {
                       @Content(mediaType = "application/json",
                                schema = @Schema(implementation = String.class))
                     })
        ,
            @ApiResponse(responseCode = "500",
                         description = "Uncontrolled error appeared",
                         content = { @Content(mediaType = "application/json") })
      })
  public ResponseEntity<String>
  getHello() {
    // Send a message to RabbitMQ
    rabbitMQService.sendMessage("Hello from AuthService!");

    // Receive a message from RabbitMQ
    String receivedMessage = rabbitMQService.receiveMessage();

    return ResponseEntity.ok("Hello, World! Received: " + receivedMessage);
  }
}
