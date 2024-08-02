package com.ewallet.transaction_microservice.repository;

import com.ewallet.transaction_microservice.enums.TransactionStatus;
import com.ewallet.transaction_microservice.model.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Transactional
    @Modifying
    @Query("update Transaction t set t.transactionStatus = :status, t.transactionStatusMsg = :statusMsg where t.transactionId = :transactionId")
    public void updateTransactionStatus(TransactionStatus status, String statusMsg, String transactionId);
}
