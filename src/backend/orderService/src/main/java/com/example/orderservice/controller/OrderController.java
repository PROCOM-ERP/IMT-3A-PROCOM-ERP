package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderCreationRequestDto;
import com.example.orderservice.dto.OrdersByIdLoginProfileResponseDto;
import com.example.orderservice.model.Path;
import com.example.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(Path.V1_ORDERS)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(operationId = "createOrder", tags = {"orders"},
            summary = "Create a new order", description =
            "Create a new order by providing location information," +
            "employee information, provider, quote, and products (see body type).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description =
                    "Order created correctly",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description =
                    "The request body is badly structured or formatted",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<String> createOrder(OrderCreationRequestDto orderDto)
            throws Exception
    {
        // try to create a new entity
        Integer idOrder = orderService.createOrder(orderDto);
        // generate URI location to inform the client how to get information on the new entity
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(Path.ORDER_ID)
                .buildAndExpand(idOrder)
                .toUri();
        // send the response with 201 Http status
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    @Operation(operationId = "getAllOrdersByIdLoginProfile", tags = {"orders"},
            summary = "Retrieve all orders for a specific user", description =
            "Retrieve all orders for a specific user, by providing its id in the query string.<br>" +
            "Orders will be grouped by those ordered by the user and those approved by the user.",
            parameters = {
                @Parameter(name = "idLoginProfile", description =
                        "The user id in the query string")})
            @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "User orders retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrdersByIdLoginProfileResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json")} )})
    public ResponseEntity<OrdersByIdLoginProfileResponseDto> getAllOrdersByIdLoginProfile(
            @RequestParam("idLoginProfile") String idLoginProfile)
    {
        return ResponseEntity.ok(orderService.getAllOrdersByIdLoginProfile(idLoginProfile));
    }

}
