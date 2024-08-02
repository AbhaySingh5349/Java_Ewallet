package com.ewallet.transaction_microservice.consumer;

// we can use either of "@Service or @Component"
// only 1 consumer from a particular "groupId" will consume msg

import com.ewallet.transaction_microservice.constants.KafkaConstants;
import com.ewallet.transaction_microservice.constants.TransactionUpdatedConstant;
import com.ewallet.transaction_microservice.enums.TransactionStatus;
import com.ewallet.transaction_microservice.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionUpdatedConsumer {
    ObjectMapper objectMapper;
    TransactionRepository transactionRepository;

    @Autowired
    public TransactionUpdatedConsumer(ObjectMapper objectMapper, TransactionRepository transactionRepository) {
        this.objectMapper = objectMapper;
        this.transactionRepository = transactionRepository;
    }

    @KafkaListener(topics = KafkaConstants.TRANSACTION_UPDATED_TOPIC, groupId = "transaction-group")
    public void transactionInitiated(String msg) throws JsonProcessingException{
        log.info("transaction updated msg consumed by transaction service: {}", msg);

        ObjectNode objectNode = objectMapper.readValue(msg, ObjectNode.class);

        String status = objectNode.get(TransactionUpdatedConstant.STATUS).textValue();
        String statusMsg = objectNode.get(TransactionUpdatedConstant.STATUS_MSG).textValue();
        String transactionId = objectNode.get(TransactionUpdatedConstant.TRANSACTION_ID).textValue();

        transactionRepository.updateTransactionStatus(TransactionStatus.valueOf(status), statusMsg, transactionId);

        log.info("transaction status updated successfully");
    }
}
