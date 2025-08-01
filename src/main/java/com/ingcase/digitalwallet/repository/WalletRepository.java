package com.ingcase.digitalwallet.repository;

import com.ingcase.digitalwallet.model.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findByCustomerId(Long customerId);
    Optional<Wallet> findByCustomerIdAndId(Long customerId, Long Id);

    @Query("SELECT t.wallet FROM Transaction t WHERE t.id = :transactionId")
    Wallet findByTransactionId(@Param("transactionId") Long transactionId);
}
