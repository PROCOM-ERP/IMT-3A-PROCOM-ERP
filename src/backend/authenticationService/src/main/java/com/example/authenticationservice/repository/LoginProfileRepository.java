package com.example.authenticationservice.repository;

import com.example.authenticationservice.model.LoginProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface LoginProfileRepository extends JpaRepository<LoginProfile, String>, JpaSpecificationExecutor<LoginProfile> {

    @Query(value = "SELECT nextval('public.login_profiles_id_login_profile_gen_seq')", nativeQuery = true)
    Integer getNextIdLoginProfile();

    @Transactional
    @Modifying
    @Query("update LoginProfile lp " +
            "set lp.password = :password, " +
            "lp.jwtGenMinAt = current_timestamp " +
            "where lp.id = :id")
    int updatePasswordById(@Param("id") String id, @NonNull @Param("password") String password);

    @Transactional
    @Modifying
    @Query(value =
            "update LoginProfile lp " +
            "set lp.email = :email " +
            "where lp.id = :id")
    int updateEmailById(@NonNull @Param("id") String id,
                        @Param("email") String email);

    @Transactional
    @Modifying
    @Query(value =
            "update LoginProfile lp " +
            "set lp.isEnable = :isEnable " +
            "where lp.id = :id")
    int updateEnableById(@NonNull @Param("id") String id,
                         @NonNull @Param("isEnable") Boolean isEnable);

    @Transactional
    @Modifying
    @Query("update LoginProfile lp " +
            "set lp.jwtGenMinAt = current_timestamp ")
    void updateAllJwtGenMinAt();

}