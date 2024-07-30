package com.ewallet.wallet_microservice.consumer;

import com.ewallet.wallet_microservice.constants.KafkaConstants;
import com.ewallet.wallet_microservice.constants.UserCreationTopicConstants;
import com.ewallet.wallet_microservice.model.Wallet;
import com.ewallet.wallet_microservice.repository.WalletRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// we can use either of "@Service or @Component"
// only 1 consumer from a particular "groupId" will consume msg

@Component
@Slf4j
public class UserCreationConsumer {
    ObjectMapper objectMapper;
    WalletRepository walletRepository;

    @Value("${wallet.initial.amount}")
    Double walletAmount;

    @Autowired
    public UserCreationConsumer(ObjectMapper objectMapper, WalletRepository walletRepository) {
        this.objectMapper = objectMapper;
        this.walletRepository = walletRepository;
    }

    @KafkaListener(topics = KafkaConstants.USER_CREATION_TOPIC, groupId = "wallet-group")
    public void userCreated(String msg) throws JsonProcessingException {
        log.info("user created msg received: {}", msg);

        ObjectNode objectNode = objectMapper.readValue(msg, ObjectNode.class);

        String phoneNum = objectNode.get(UserCreationTopicConstants.PHONENUM).textValue();
        Integer userId = objectNode.get(UserCreationTopicConstants.USERID).intValue();

        Wallet wallet = Wallet.builder()
                .phoneNum(phoneNum)
                .userId(userId)
                .balance(walletAmount)
                .build();

        walletRepository.save(wallet);

        log.info("wallet: {} saved for userId: {}", wallet, userId);
    }
}
