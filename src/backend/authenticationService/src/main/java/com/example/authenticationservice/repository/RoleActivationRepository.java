package com.example.authenticationservice.repository;

import com.example.authenticationservice.model.RoleActivation;
import com.example.authenticationservice.model.RoleActivationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoleActivationRepository extends JpaRepository<RoleActivation, RoleActivationId>, JpaSpecificationExecutor<RoleActivation> {
}