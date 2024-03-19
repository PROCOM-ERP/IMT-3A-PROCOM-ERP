package com.example.orderservice.repository;

import com.example.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {

    @Query(value =
            "SELECT o " +
                    "FROM Order o " +
                    "WHERE o.orderer.id = :idOrderer")
    Set<Order> findAllByOrderer(@NonNull @Param("idOrderer") Integer idOrderer);

    @Query(value =
            "SELECT o " +
            "FROM Order o " +
            "WHERE o.approver.id = :idApprover")
    Set<Order> findAllByApprover(@NonNull @Param("idApprover") Integer idApprover);

    @Transactional
    @Modifying
    @Query(value =
            "update orders " +
            "set approver = :idApprover, " +
            "progress_status = 2 " +
            "where orderer in :idsOrderer and progress_status < :progressStatus", nativeQuery = true)
    int updateAllByOrdererAndProgressStatusLessThan(@Param("idsOrderer") @NonNull Set<Integer> idsOrderer,
                                                    @Param("idApprover") @NonNull Integer idApprover,
                                                    @Param("progressStatus") @NonNull Integer progressStatus);

}