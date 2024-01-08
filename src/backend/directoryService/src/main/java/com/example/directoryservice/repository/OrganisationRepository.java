package com.example.directoryservice.repository;

import com.example.directoryservice.model.Address;
import com.example.directoryservice.model.Organisation;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OrganisationRepository extends JpaRepository<Organisation, Integer>, JpaSpecificationExecutor<Organisation> {

    Optional<Organisation> findByName(@NonNull String name);

    boolean existsByName(@NonNull String name);

    void deleteByName(@NonNull String name);

    @Transactional
    @Modifying
    @Query(value =
            "update organisations " +
            "set address = :idAddress " +
            "where id = :idOrganisation",
            nativeQuery = true)
    int updateAddressById(@NonNull @Param("idOrganisation") Integer idOrganisation,
                          @NonNull @Param("idAddress") Integer idAddress);

    @Transactional
    @Modifying
    @Query(value =
            "update organisations " +
            "set address = :idAddress " +
            "where name = :name",
            nativeQuery = true)
    int updateAddressByName(@NonNull @Param("name") String name,
                            @NonNull @Param("idAddress") Integer idAddress);


}