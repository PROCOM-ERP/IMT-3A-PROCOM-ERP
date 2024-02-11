package com.example.authenticationservice.repository;

import com.example.authenticationservice.model.Role;
import org.springframework.lang.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {

    @Query(value =
        "SELECT r.name " +
        "FROM Role r ")
    Set<String> findAllRoleNames();

    List<Role> findAllByNameIn(@NonNull Set<String> roleNames);

    @Query("SELECT DISTINCT p " +
            "FROM Role r JOIN r.permissions p " +
            "WHERE r.isEnable = true AND r.name IN :roleNames")
    Set<String> findDistinctPermissionsByRoleNames(@NonNull List<String> roleNames);
}