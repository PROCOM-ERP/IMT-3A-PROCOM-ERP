package com.example.authenticationservice.repository;

import com.example.authenticationservice.model.RoleActivation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoleActivationRepository extends JpaRepository<RoleActivation, Integer>, JpaSpecificationExecutor<RoleActivation> {
}