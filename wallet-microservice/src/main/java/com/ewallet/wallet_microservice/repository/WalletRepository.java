package com.ewallet.wallet_microservice.repository;

import com.ewallet.wallet_microservice.model.Wallet;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Wallet findByPhoneNum(String phoneNum);

    @Transactional
    @Modifying
    @Query("update Wallet w set w.balance = w.balance + :amount where w.phoneNum = :phoneNum")
    void updateWallet(String phoneNum, Double amount);
}
