package com.ingcase.digitalwallet.repository;

import com.ingcase.digitalwallet.model.entity.Transaction;
import com.ingcase.digitalwallet.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByWalletId(Long walletId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Transaction t SET t.status = :status WHERE t.id = :id")
    void updateStatusById(@Param("id") Long id, @Param("status") Status status);

    @Query("SELECT t FROM Transaction t WHERE t.id = :id and t.status = 'PENDING'")
    Optional<Transaction> findByIdAndStatusPending(@Param("id") Long id);
}