package com.example.orderservice.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Path.V1_ORDERS)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

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
