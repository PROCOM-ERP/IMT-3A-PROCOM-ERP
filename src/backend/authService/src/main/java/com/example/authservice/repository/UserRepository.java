package com.example.authservice.repository;

import com.example.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    boolean existsByIdUser(String idUser);

    @Query(value = "SELECT nextval('public.users_index_user_seq')", nativeQuery = true)
    Integer getNextValIndexUser();

    @Transactional
    @Modifying
    @Query("update User u set u.password = :password where u.idUser = :idUser")
    int updateUserPasswordByIdUser(@Param("idUser") String idUser, @NonNull @Param("password") String password);

}