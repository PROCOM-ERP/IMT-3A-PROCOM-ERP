package com.example.directoryservice.repository;

import com.example.directoryservice.model.LoginProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface LoginProfileRepository extends JpaRepository<LoginProfile, String>, JpaSpecificationExecutor<LoginProfile> {

    @Transactional
    @Modifying
    @Query(value =
            "update login_profiles " +
                    "set jwt_gen_min_at = current_timestamp " +
                    "where id = :id",
            nativeQuery = true)
    void updateJwtGenMinAtById(@NonNull @Param("id") String id);

    @Transactional
    @Modifying
    @Query(value =
            "update login_profiles " +
                    "set jwt_gen_min_at = current_timestamp ", nativeQuery = true)
    void updateAllJwtGenMinAt();

    @Override
    boolean existsById(@NonNull String idLoginProfile);
}