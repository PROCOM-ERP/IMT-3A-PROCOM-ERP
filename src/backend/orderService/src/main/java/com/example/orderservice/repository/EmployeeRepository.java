package com.example.orderservice.repository;

import com.example.orderservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

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
            "WHERE e.loginProfile.id = :idLoginProfile AND " +
                    "e.lastName = :lastName AND " +
                    "e.firstName = :firstName AND " +
                    "e.email = :email AND " +
                    "e.phoneNumber = :phoneNumber")
    Optional<Employee> findLastCreatedEmployeeMatchingCriteria(
            @NonNull @Param("idLoginProfile") String idLoginProfile,
            @NonNull @Param("lastName") String lastName,
            @NonNull @Param("firstName") String firstName,
            @NonNull @Param("email") String email,
            @NonNull @Param("phoneNumber") String phoneNumber);

}