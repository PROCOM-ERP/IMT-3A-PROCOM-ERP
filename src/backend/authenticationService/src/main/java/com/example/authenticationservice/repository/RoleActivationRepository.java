package com.example.authenticationservice.repository;

import com.example.authenticationservice.model.RoleActivation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface RoleActivationRepository extends JpaRepository<RoleActivation, Integer>, JpaSpecificationExecutor<RoleActivation> {

    @Query(value =
        "SELECT DISTINCT ra.microservice " +
        "FROM RoleActivation ra "
    )
    Set<String> findAllMicroservices();
}