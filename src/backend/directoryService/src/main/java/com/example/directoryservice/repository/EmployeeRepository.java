package com.example.directoryservice.repository;

import com.example.directoryservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, String>, JpaSpecificationExecutor<Employee> {

    Optional<Employee> findByEmail(@NonNull String email);

    @Transactional
    @Modifying
    @Query(value =
            "update Employee e " +
            "set e.lastName = :lastName, "+
            "e.firstName = :firstName, "+
            "e.email = :email, "+
            "e.phoneNumber = :phoneNumber "+
            "where e.id = :idEmployee")
    int updateInfoById(@NonNull @Param("idEmployee") String idEmployee,
                       @NonNull @Param("lastName") String lastName,
                       @NonNull @Param("firstName") String firstName,
                       @NonNull @Param("email") String email,
                       @Param("phoneNumber") String phoneNumber);

    @Transactional
    @Modifying
    @Query(value =
            "update Employee e " +
            "set e.lastName = :lastName, "+
            "e.firstName = :firstName, "+
            "e.email = :email, "+
            "e.phoneNumber = :phoneNumber "+
            "where e.email = :email")
    int updateInfoByEmail(@NonNull @Param("email") String email,
                          @NonNull @Param("lastName") String lastName,
                          @NonNull @Param("firstName") String firstName,
                          @Param("phoneNumber") String phoneNumber);

    @Transactional
    @Modifying
    @Query(value =
            "update employees " +
            "set service = :idService " +
            "where id = :idEmployee",
            nativeQuery = true)
    int updateServiceById(@NonNull @Param("idEmployee") String idEmployee,
                          @NonNull @Param("idService") Integer idService);

    @Transactional
    @Modifying
    @Query(value =
            "update employees " +
            "set service = :idService " +
            "where email = :email",
            nativeQuery = true)
    int updateServiceByEmail(@NonNull @Param("email") String email,
                             @NonNull @Param("idService") Integer idService);

    @Transactional
    @Modifying
    @Query(value =
            "update employees " +
            "set enable = :enable " +
            "where id = :idEmployee",
            nativeQuery = true)
    int updateEnableById(@NonNull @Param("idEmployee") String idEmployee,
                         @NonNull @Param("enable") Boolean enable);

    @Transactional
    @Modifying
    @Query(value =
            "update employees " +
            "set enable = :enable " +
            "where email = :email",
            nativeQuery = true)
    int updateEnableByEmail(@NonNull @Param("email") String email,
                            @NonNull @Param("enable") Boolean enable);

    @Transactional
    @Modifying
    @Query(value =
            "update employees " +
            "set jwt_min_creation = current_timestamp",
            nativeQuery = true)
    void updatAllJwtMinCreation();

    @Transactional
    @Modifying
    @Query(value =
            "update employees " +
            "set jwt_min_creation = current_timestamp " +
            "where id = :idEmployee",
            nativeQuery = true)
    int updateJwtMinCreationById(@NonNull @Param("idEmployee") String idEmployee);

}