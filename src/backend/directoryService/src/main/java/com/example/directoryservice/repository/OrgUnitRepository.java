package com.example.directoryservice.repository;

import com.example.directoryservice.model.OrgUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrgUnitRepository extends JpaRepository<OrgUnit, Integer>, JpaSpecificationExecutor<OrgUnit> {
}