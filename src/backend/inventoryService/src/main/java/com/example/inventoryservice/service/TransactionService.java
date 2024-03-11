package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.TransactionDto;
import com.example.inventoryservice.model.Transaction;
import com.example.inventoryservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    static TransactionDto transactionToDto(Transaction transaction){
        return TransactionDto.builder()
                .id(transaction.getId())
                .employee(transaction.getEmployee())
                .quantity(transaction.getQuantity())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}
