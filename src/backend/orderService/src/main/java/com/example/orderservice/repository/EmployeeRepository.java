package com.example.orderservice.repository;

import com.example.orderservice.model.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmployeeRepository extends JpaRepository<Employee, Integer>, JpaSpecificationExecutor<Employee> {

    @Query(value =
            "SELECT e.id " +
            "FROM Employee e " +
            "WHERE e.loginProfile.id = :idLoginProfile")
    Set<Integer> findAllIdsByIdLoginProfile(@NonNull @Param("idLoginProfile") String idLoginProfile);

    @Query(value =
            "SELECT e " +
            "FROM Employee e " +
            "WHERE e.loginProfile.id = :idLoginProfile " +
            "ORDER BY e.createdAt DESC")
    Set<Employee> findAllEmployeesById(@NonNull @Param("idLoginProfile") String idLoginProfile);

    @Query(value =
            "SELECT e " +
            "FROM Employee e " +
            "WHERE e.loginProfile.id = :idLoginProfile " +
            "ORDER BY e.createdAt DESC " +
            "LIMIT 1")
    Optional<Employee> findLastEmployeeById(@NonNull @Param("idLoginProfile") String idLoginProfile);

    @Query("SELECT e, a " +
            "FROM Employee e " +
            "JOIN Order o ON e.id = o.orderer.id " +
            "JOIN Address a ON o.address.id = a.id " +
            "WHERE e.loginProfile.id = :idLoginProfile " +
            "ORDER BY o.createdAt DESC")
    List<Object[]> findEmployeeAndLastOrderAddressByIdLoginProfile(@Param("idLoginProfile") String idLoginProfile, Pageable pageable);

}