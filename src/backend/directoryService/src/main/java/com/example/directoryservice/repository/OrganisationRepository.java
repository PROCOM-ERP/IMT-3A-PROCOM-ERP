package com.example.directoryservice.repository;

import com.example.directoryservice.model.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrganisationRepository extends JpaRepository<Organisation, Integer>, JpaSpecificationExecutor<Organisation> {
}