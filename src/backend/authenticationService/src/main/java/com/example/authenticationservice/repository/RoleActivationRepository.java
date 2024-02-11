package com.example.authenticationservice.repository;

import com.example.authenticationservice.model.RoleActivation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.Set;

public interface RoleActivationRepository extends JpaRepository<RoleActivation, Integer>, JpaSpecificationExecutor<RoleActivation> {

    @Query(value =
        "SELECT DISTINCT ra.microservice " +
        "FROM RoleActivation ra "
    )
    Set<String> findAllMicroservices();

    @Query(value =
        "SELECT ra " +
        "FROM RoleActivation ra " +
        "WHERE ra.role.name = :role AND ra.microservice = :microservice"
    )
    Optional<RoleActivation> findByRoleAndMicroservice(@Param("role") @NonNull String role,
                                                       @Param("microservice") @NonNull String microservice);
}