package com.example.directoryservice.repository;

import com.example.directoryservice.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AddressRepository extends JpaRepository<Address, String>, JpaSpecificationExecutor<Address> {
}