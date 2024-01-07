package com.example.directoryservice.repository;

import com.example.directoryservice.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface ServiceRepository extends JpaRepository<Service, Integer>, JpaSpecificationExecutor<Service> {
    @Transactional
    @Modifying
    @Query(value =
            "update services " +
            "set address = :idAddress " +
            "where id = :idService",
            nativeQuery = true)
    int updateAddressById(@NonNull @Param("idService") Integer idService,
                          @NonNull @Param("idAddress") Integer idAddress);


    @Transactional
    @Modifying
    @Query(value =
            "update services " +
            "set organisation = :idOrganisation " +
            "where id = :idService",
            nativeQuery = true)
    int updateOrganisationById(@NonNull @Param("idService") Integer idService,
                               @NonNull @Param("idOrganisation") Integer idOrganisation);

}