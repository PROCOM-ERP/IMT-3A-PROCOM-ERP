package com.example.directoryservice.repository;

import com.example.directoryservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {

    @Query("SELECT DISTINCT p " +
            "FROM Role r JOIN r.permissions p " +
            "WHERE r.isEnable = true AND r.name IN :roleNames")
    Set<String> findDistinctPermissionsByRoleNames(@NonNull List<String> roleNames);

}