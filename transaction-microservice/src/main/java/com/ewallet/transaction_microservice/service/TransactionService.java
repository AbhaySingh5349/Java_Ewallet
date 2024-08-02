package com.ewallet.transaction_microservice.service;

import com.ewallet.transaction_microservice.client.UserServiceClient;
import com.ewallet.transaction_microservice.constants.KafkaConstants;
import com.ewallet.transaction_microservice.constants.TransactionInitiatedTopicConstants;
import com.ewallet.transaction_microservice.dto.InitiateTransactionRequest;
import com.ewallet.transaction_microservice.enums.TransactionStatus;
import com.ewallet.transaction_microservice.model.Transaction;
import com.ewallet.transaction_microservice.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TransactionService implements UserDetailsService {
    UserServiceClient userServiceClient;
    TransactionRepository transactionRepository;
    KafkaTemplate kafkaTemplate; // properties are mentioned in application.config & its bean will get created at runtime
    ObjectMapper objectMapper;

    @Autowired
    public TransactionService(UserServiceClient userServiceClient, TransactionRepository transactionRepository, KafkaTemplate kafkaTemplate, ObjectMapper objectMapper) {
        this.userServiceClient = userServiceClient;
        this.transactionRepository = transactionRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNum) throws UsernameNotFoundException {
        String auth = "txn_service:txn_service"; // "username:password" encoded by postman to Base64 encoding
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());

        String basicAuth = "Basic " + new String(encodedAuth);

        ObjectNode objectNode = userServiceClient.getUser(phoneNum, basicAuth);

        log.info("sender user details fetched through UserServiceClient: {}", objectNode);

        if(objectNode == null){
            throw new UsernameNotFoundException("user does not exist");
        }

        ArrayNode authorities = (ArrayNode) objectNode.get("authorities");

        final List<GrantedAuthority> authorityList = new ArrayList<>();

        authorities.forEach(jsonNode -> {
            authorityList.add(new SimpleGrantedAuthority(jsonNode.get("authority").textValue()));
        });

        User sender = new User(objectNode.get("phoneNum").textValue(), objectNode.get("password").textValue(), authorityList);

        log.info("authenticated sender user details: {}", sender);

        return sender;
    }


    public String initiateTransaction(String senderPhoneNum, InitiateTransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .senderPhoneNum(senderPhoneNum)
                .receiverPhoneNum(request.getReceiverPhoneNum())
                .amount(request.getAmount())
                .purpose(request.getPurpose())
                .transactionStatusMsg("Transaction initiated")
                .transactionStatus(TransactionStatus.INITIATED)
                .build();

        transactionRepository.save(transaction);

        log.info("transaction saved: {}", transaction);

        // publish data to kafka
        // fields required by wallet service for processing ? senderPhoneNum, receiverPhoneNum, amount, transactionId

        // creating key-value pairs
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put(TransactionInitiatedTopicConstants.SENDER_PHONE_NUM, transaction.getSenderPhoneNum());
        objectNode.put(TransactionInitiatedTopicConstants.RECEIVER_PHONE_NUM, transaction.getReceiverPhoneNum());
        objectNode.put(TransactionInitiatedTopicConstants.AMOUNT, transaction.getAmount());
        objectNode.put(TransactionInitiatedTopicConstants.TRANSACTION_ID, transaction.getTransactionId());

        String kafkaMsg = objectNode.toString();
        kafkaTemplate.send(KafkaConstants.TRANSACTION_INITIATED_TOPIC, kafkaMsg);

        log.info("transaction service published initiated msg to wallet service: {}", kafkaMsg);

        return transaction.getTransactionId();
    }

    public List<Transaction> getTransactionsInitiatedByLoggedInUser(String phoneNum, int pageNum, int limit) {
        PageRequest pageRequest = PageRequest.of(pageNum, limit);

//        Page<Transaction> response = transactionRepository.findBySenderPhoneNum(phoneNum, pageRequest);

        return transactionRepository.findBySenderPhoneNum(phoneNum, pageRequest);
    }
}
