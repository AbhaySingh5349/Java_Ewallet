package com.ewallet.transaction_microservice.controller;

import com.ewallet.transaction_microservice.dto.InitiateTransactionRequest;
import com.ewallet.transaction_microservice.model.Transaction;
import com.ewallet.transaction_microservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/transaction")
public class TransactionController {
    TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/")
    public String initiateTransaction(@RequestBody @Valid InitiateTransactionRequest request){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String senderPhoneNum = userDetails.getUsername();

        return transactionService.initiateTransaction(senderPhoneNum, request);
    }

    @GetMapping("/all")
    public List<Transaction> getTransactionsInitiatedByLoggedInUser(@RequestParam("pageNum") int pageNum,
                                                                    @RequestParam("limit") int limit){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String phoneNum = userDetails.getUsername();

        return transactionService.getTransactionsInitiatedByLoggedInUser(phoneNum, pageNum, limit);
    }
}
