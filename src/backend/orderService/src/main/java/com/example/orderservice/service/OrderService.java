package com.example.orderservice.service;


import com.example.orderservice.dto.OrderByApproverResponseDto;
import com.example.orderservice.dto.OrderByOrdererResponseDto;
import com.example.orderservice.dto.OrdersByIdLoginProfileResponseDto;
import com.example.orderservice.model.Order;
import com.example.orderservice.repository.EmployeeRepository;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;

    /* Public Methods */
    public OrdersByIdLoginProfileResponseDto getAllOrdersByIdLoginProfile(String idLoginProfile) {

        // retrieve all Employee entities id for the LoginProfile's id provided
        Set<Integer> idEmployees = employeeRepository.findAllIdsByIdLoginProfile(idLoginProfile);

        // retrieve all Order entities for all Employee ids where ids correspond to orderer
        Set<OrderByOrdererResponseDto> ordersByOrderer = new LinkedHashSet<>();
        idEmployees.forEach(idOrderer ->
                ordersByOrderer.addAll(
                        orderRepository.findAllByOrderer(idOrderer).stream()
                                .map(this::modelToOrderByOrdererResponseDto)
                                .collect(Collectors.toSet())));

        // retrieve all Order entities for all Employee ids where ids correspond to approver
        Set<OrderByApproverResponseDto> ordersByApprover = new LinkedHashSet<>();
        idEmployees.forEach(idApprover ->
                ordersByApprover.addAll(
                        orderRepository.findAllByApprover(idApprover).stream()
                                .map(this::modelToOrderByApproverResponseDto)
                                .collect(Collectors.toSet())));

        // build OrdersByIdLoginProfileResponseDto entity
        OrdersByIdLoginProfileResponseDto ordersDto = OrdersByIdLoginProfileResponseDto.builder()
                .ordersByOrderer(ordersByOrderer)
                .ordersByApprover(ordersByApprover)
                .build();

        // return OrdersByIdLoginProfileResponseDto entity
        return ordersDto;
    }

    /* Private Methods */
    private OrderByOrdererResponseDto modelToOrderByOrdererResponseDto(Order order) {
        return OrderByOrdererResponseDto.builder()
                .id(order.getId())
                .createdAt(Date.from(order.getCreatedAt()))
                .provider(order.getProvider().getName())
                .totalAmount(order.getTotalAmount())
                .approver(order.getApprover().getFirstName() + " " + order.getApprover().getLastName())
                .status(order.getProgressStatus().getName())
                .build();
    }

    private OrderByApproverResponseDto modelToOrderByApproverResponseDto(Order order) {
        return OrderByApproverResponseDto.builder()
                .id(order.getId())
                .createdAt(Date.from(order.getCreatedAt()))
                .provider(order.getProvider().getName())
                .totalAmount(order.getTotalAmount())
                .orderer(order.getOrderer().getFirstName() + " " + order.getOrderer().getLastName())
                .status(order.getProgressStatus().getName())
                .build();
    }

}
