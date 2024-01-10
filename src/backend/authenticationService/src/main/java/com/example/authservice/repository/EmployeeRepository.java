package com.example.authservice.repository;

import com.example.authservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface EmployeeRepository extends JpaRepository<Employee, String>, JpaSpecificationExecutor<Employee> {

    @Query(value = "SELECT nextval('public.employees_id_employee_gen_seq')", nativeQuery = true)
    Integer getNextIdEmployee();

    @Transactional
    @Modifying
    @Query("update Employee e " +
            "set e.password = :password, " +
            "e.jwtMinCreation = current_timestamp " +
            "where e.id = :id")
    int updatePasswordById(@Param("id") String id, @NonNull @Param("password") String password);

    @Transactional
    @Modifying
    @Query(value =
            "update Employee e " +
            "set e.email = :email " +
            "where e.id = :idEmployee")
    int updateEmailById(@NonNull @Param("idEmployee") String idEmployee,
                        @Param("email") String email);

    @Transactional
    @Modifying
    @Query(value =
            "update Employee e " +
            "set e.enable = :enable " +
            "where e.id = :idEmployee")
    int updateEnableById(@NonNull @Param("idEmployee") String idEmployee,
                         @NonNull @Param("enable") Boolean enable);

}