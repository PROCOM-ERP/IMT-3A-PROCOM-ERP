package com.example.orderservice.service;


import com.example.orderservice.annotation.LogExecutionTime;
import com.example.orderservice.dto.*;
import com.example.orderservice.model.*;
import com.example.orderservice.repository.*;
import com.example.orderservice.utils.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProgressStatusRepository progressStatusRepository;
    private final ProviderRepository providerRepository;
    private final EmployeeService employeeService;
    private final AddressService addressService;

    /* Public Methods */
    @Transactional
    @LogExecutionTime(description = "Create new order.",
            tag = CustomLogger.TAG_ORDERS)
    public Integer createOrder(OrderCreationRequestDto orderDto)
            throws Exception
    {
        // retrieve Address entity if it already exists, else create and save it
        Address address = addressService.createAddress(orderDto.getAddress());

        // retrieve Employee entity if it already exists, else create and save it
        Employee orderer = employeeService.createEmployee(orderDto.getOrderer());

        // retrieve all other field entities
        ProgressStatus defaultProgressStatus = progressStatusRepository.findById(1).orElseThrow();
        Provider provider = providerRepository.findById(orderDto.getProvider()).orElseThrow();

        // calculate the Order entity total amount
        BigDecimal totalAmount = orderDto.getProducts().stream()
                .map(product -> product.getUnitPrice().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        // create and save Order entity
        // TODO: change approver by the retrieving one from other microservice
        Order order = orderRepository.save(creationRequestDtoToModel(orderDto, totalAmount,
                provider, address, orderer, defaultProgressStatus, orderer));

        // create and save OrderProduct entities
        orderProductRepository.saveAll(
                orderDto.getProducts().stream()
                        .map(op -> orderProductCreationRequestDtoToModel(order, op))
                        .toList());

        // return Order entity id
        return order.getId();

    }

    @LogExecutionTime(description = "Retrieve all orders that a user has placed or needs to approve.",
            tag = CustomLogger.TAG_ORDERS)
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

        // build and returnOrdersByIdLoginProfileResponseDto entity
        return OrdersByIdLoginProfileResponseDto.builder()
                .ordersByOrderer(ordersByOrderer)
                .ordersByApprover(ordersByApprover)
                .build();
    }

    public OrderResponseDto getOrderById(Integer idOrder)
            throws NoSuchElementException
    {
        // retrieve Order entity
        Order order = orderRepository.findById(idOrder)
                .orElseThrow(() -> new NoSuchElementException("No existing order with id " + idOrder + "."));

        // check if the LoginProfile of the authenticated user is the orderer,
        // or the approver of the order (or admin)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentLoginProfileId = authentication.getName();
        boolean canBypassAccessDeny = authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority(Permission.CanBypassAccessDeny.name()));
        if (! (currentLoginProfileId.equals(order.getOrderer().getLoginProfile().getId()) ||
            canBypassAccessDeny ||
                (order.getApprover() != null &&
            currentLoginProfileId.equals(order.getApprover().getLoginProfile().getId()))))
        {
            throw new AccessDeniedException("");
        }

        // retrieve all ProgressStatus entities and check which ones are already completed
        List<ProgressStatusResponseDto> allProgressStatus = progressStatusRepository.findAll().stream()
                .map(ps -> progressStatusModelToResponseDto(ps, order.getProgressStatus().getId()))
                .sorted(Comparator.comparingInt(ProgressStatusResponseDto::getId))
                .toList();

        // convert all OrderProduct entities into OrderProductResponseDto entities
        List<OrderProductResponseDto> products = order.getOrderProducts().stream()
                .map(this::orderProductModelToResponseDto)
                .toList();

        // build and return OrderResponseDto entity
        return modelToResponseDto(order, products, allProgressStatus);
    }

    /* Private Methods */
    private Order creationRequestDtoToModel(OrderCreationRequestDto orderDto, BigDecimal totalAmount,
                                            Provider provider, Address address, Employee orderer,
                                            ProgressStatus progressStatus, Employee approver)
    {
        return Order.builder()
                .quote(orderDto.getQuote())
                .totalAmount(totalAmount)
                .provider(provider)
                .address(address)
                .orderer(orderer)
                .progressStatus(progressStatus)
                .approver(approver)
                .build();
    }


    private OrderProduct orderProductCreationRequestDtoToModel(
            Order order,
            OrderProductCreationRequestDto orderProductDto)
    {
        return OrderProduct.builder()
                .reference(orderProductDto.getReference())
                .unitPrice(orderProductDto.getUnitPrice())
                .quantity(orderProductDto.getQuantity())
                .order(order)
                .build();
    }


    private OrderByOrdererResponseDto modelToOrderByOrdererResponseDto(Order order) {
        return OrderByOrdererResponseDto.builder()
                .id(order.getId())
                .createdAt(Date.from(order.getCreatedAt()))
                .provider(order.getProvider().getName())
                .totalAmount(order.getTotalAmount())
                .approver(order.getApprover() != null ?
                        order.getApprover().getFirstName() + " " + order.getApprover().getLastName() :
                        null)
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

    private OrderResponseDto modelToResponseDto(Order order,
                                                List<OrderProductResponseDto> products,
                                                List<ProgressStatusResponseDto> progressStatus)
    {
        return OrderResponseDto.builder()
                .id(order.getId())
                .createdAt(Date.from(order.getCreatedAt()))
                .provider(order.getProvider().getName())
                .totalAmount(order.getTotalAmount())
                .orderer(order.getOrderer().getLoginProfile().getId())
                .approver(order.getApprover() != null ?  order.getApprover().getLoginProfile().getId() : null)
                .progress(progressStatus)
                .products(products)
                .build();
    }

    private ProgressStatusResponseDto progressStatusModelToResponseDto(ProgressStatus progressStatus,
                                                                       Integer currentIdProgressStatus)
    {
        return ProgressStatusResponseDto.builder()
                .id(progressStatus.getId())
                .name(progressStatus.getName())
                .completed(progressStatus.getId() <= currentIdProgressStatus)
                .build();
    }

    private OrderProductResponseDto orderProductModelToResponseDto(OrderProduct orderProduct)
    {
        return OrderProductResponseDto.builder()
                .reference(orderProduct.getReference())
                .unitPrice(orderProduct.getUnitPrice())
                .quantity(orderProduct.getQuantity())
                .build();
    }

}
