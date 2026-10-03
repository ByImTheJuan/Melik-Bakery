package com.hyd.pipes_bakery_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hyd.pipes_bakery_backend.model.PaymentTransaction;

import jakarta.persistence.LockModeType;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByWompiReference(String wompiReference);

    boolean existsByWompiReference(String wompiReference);

    // Serialises concurrent updates of one attempt (webhook vs. status check) so an order is created only once
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM PaymentTransaction t WHERE t.wompiReference = :wompiReference")
    Optional<PaymentTransaction> findByWompiReferenceForUpdate(@Param("wompiReference") String wompiReference);
}
