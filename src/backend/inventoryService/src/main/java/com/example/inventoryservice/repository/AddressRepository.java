package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findAllByGroupId(int groupId);
}
