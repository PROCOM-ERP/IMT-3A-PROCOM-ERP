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
    @Query(value =
            "update login_profiles " +
            "set password = :password, " +
            "jwt_gen_min_at = current_timestamp " +
            "where id = :idLoginProfile", nativeQuery = true)
    int updatePasswordById(@Param("idLoginProfile") String idLoginProfile,
                           @NonNull @Param("password") String password);

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
            "update login_profiles " +
                    "set jwt_gen_min_at = current_timestamp " +
                    "where id = :idLoginProfile",
            nativeQuery = true)
    int updateJwtGenMinAtById(@NonNull @Param("idLoginProfile") String idLoginProfile);

    @Transactional
    @Modifying
    @Query(value =
            "update login_profiles " +
            "set jwt_gen_min_at = current_timestamp ", nativeQuery = true)
    void updateAllJwtGenMinAt();

}