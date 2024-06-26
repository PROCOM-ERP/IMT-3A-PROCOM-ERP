package com.example.orderservice.controller;

import com.example.orderservice.dto.HttpStatusErrorDto;
import com.example.orderservice.dto.OrderCreationRequestDto;
import com.example.orderservice.dto.OrderResponseDto;
import com.example.orderservice.dto.OrdersByIdLoginProfileResponseDto;
import com.example.orderservice.model.Path;
import com.example.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(Path.V1_ORDERS)
@RequiredArgsConstructor
public class OrderController {

    /* Service Beans */

    private final OrderService orderService;

    /* Endpoints Methods */

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
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class)) }),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class)) }),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<String> createOrder(
            @Valid @RequestBody OrderCreationRequestDto orderDto)
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
                @Parameter(name = "idLoginProfile", in = ParameterIn.QUERY, description =
                        "The user id in the query string")})
            @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "User orders retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrdersByIdLoginProfileResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<OrdersByIdLoginProfileResponseDto> getAllOrdersByIdLoginProfile(
            @RequestParam("idLoginProfile") String idLoginProfile)
    {
        return ResponseEntity.ok(orderService.getAllOrdersByIdLoginProfile(idLoginProfile));
    }

    @GetMapping(Path.ORDER_ID)
    @Operation(operationId = "getOrderById", tags = {"orders"},
            summary = "Retrieve one order", description =
            "Retrieve one order, by providing its id in the path.",
            parameters = {
                    @Parameter(name = "idOrder", in = ParameterIn.PATH,description =
                            "The order id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description =
                    "Order retrieved correctly",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponseDto.class))} ),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "403", description =
                    "Forbidden to get order because authenticated user is not the orderer neither the approver",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable Integer idOrder)
    {
        return ResponseEntity.ok(orderService.getOrderById(idOrder));
    }

    @PatchMapping(Path.ORDER_ID_PROGRESS)
    @Operation(operationId = "updateOrderProgressStatusById", tags = {"orders"},
            summary = "Update the progress status of an order", description =
            "Update the progress status of an order. Only the order approver (or admin) can approve an order",
            parameters = {
                    @Parameter(name = "idOrder", in = ParameterIn.PATH, description =
                            "The order id")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description =
                    "Order progress status updated correctly",
                    content = {@Content(mediaType = "application/json")} ),
            @ApiResponse(responseCode = "400", description =
                    "The request body is badly structured or formatted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class)) }),
            @ApiResponse(responseCode = "401", description =
                    "Roles in Jwt token are insufficient to authorize the access to this URL",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "403", description =
                    "Forbidden to get order because authenticated user is not the orderer neither the approver",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "422", description =
                    "Attribute values don't respect integrity constraints.<br>" +
                    "IdProgressStatus : an integer from 1 to 5.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} ),
            @ApiResponse(responseCode = "500", description =
                    "Uncontrolled error appeared",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = HttpStatusErrorDto.class))} )})
    public ResponseEntity<String> updateOrderProgressStatusById(
            @PathVariable Integer idOrder)
    {
        orderService.updateOrderProgressStatusById(idOrder);
        return ResponseEntity.noContent().build();
    }

}
