package com.ewallet.transaction_microservice.service;

import com.ewallet.transaction_microservice.client.UserServiceClient;
import com.ewallet.transaction_microservice.dto.InitiateTransactionRequest;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class TransactionService implements UserDetailsService {
    UserServiceClient userServiceClient;

    @Autowired
    public TransactionService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
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

        return sender;
    }

    public String initiateTransaction(InitiateTransactionRequest request) {
        return "";
    }
}
