package com.example.authenticationservice.repository;

import com.example.authenticationservice.model.RoleServices;
import com.example.authenticationservice.model.RoleServiceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoleServiceRepository extends JpaRepository<RoleServices, RoleServiceId>, JpaSpecificationExecutor<RoleServices> {
}