package com.hyd.pipes_bakery_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hyd.pipes_bakery_backend.model.PaymentTransaction;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findByWompiReference(String wompiReference);

    boolean existsByWompiReference(String wompiReference);
}
