package com.example.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProgressStatus {

    Created(1),
    WaitingForApproval(2),
    Approved(3),
    WaitingForDelivery(4),
    Received(5);

    private final Integer id;
}