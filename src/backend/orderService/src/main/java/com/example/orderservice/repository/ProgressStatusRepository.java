package com.example.orderservice.repository;

import com.example.orderservice.model.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProgressStatusRepository extends JpaRepository<ProgressStatus, Integer>, JpaSpecificationExecutor<ProgressStatus> {
}