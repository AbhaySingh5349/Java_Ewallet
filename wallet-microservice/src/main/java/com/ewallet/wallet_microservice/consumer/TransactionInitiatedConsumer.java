package com.ewallet.wallet_microservice.consumer;

// we can use either of "@Service or @Component"
// only 1 consumer from a particular "groupId" will consume msg

import com.ewallet.wallet_microservice.constants.KafkaConstants;
import com.ewallet.wallet_microservice.constants.TransactionInitiatedTopicConstants;
import com.ewallet.wallet_microservice.constants.UserCreationTopicConstants;
import com.ewallet.wallet_microservice.model.Wallet;
import com.ewallet.wallet_microservice.repository.WalletRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionInitiatedConsumer {
    ObjectMapper objectMapper;
    WalletRepository walletRepository;

    @Autowired
    public TransactionInitiatedConsumer(ObjectMapper objectMapper, WalletRepository walletRepository) {
        this.objectMapper = objectMapper;
        this.walletRepository = walletRepository;
    }

    @KafkaListener(topics = KafkaConstants.TRANSACTION_INITIATED_TOPIC, groupId = "wallet-group")
    public void transactionInitiated(String msg) throws JsonProcessingException {
        log.info("transaction initiated msg consumed by kafka: {}", msg);

        ObjectNode objectNode = objectMapper.readValue(msg, ObjectNode.class);

        String senderPhoneNum = objectNode.get(TransactionInitiatedTopicConstants.SENDER_PHONE_NUM).textValue();
        String receiverPhoneNum = objectNode.get(TransactionInitiatedTopicConstants.RECEIVER_PHONE_NUM).textValue();
        Double amount = objectNode.get(TransactionInitiatedTopicConstants.AMOUNT).doubleValue();
        String transactionId = objectNode.get(TransactionInitiatedTopicConstants.TRANSACTION_ID).textValue();

        // verify if sender & receiver have wallets
        Wallet senderWallet = walletRepository.findByPhoneNum(senderPhoneNum);
        Wallet receiverWallet = walletRepository.findByPhoneNum(receiverPhoneNum);

        String status;
        String statusMsg;

        if(senderWallet == null){
            log.info("Sender wallet is not present");
            status = "FAILED";
            statusMsg = "Sender wallet does not present";
        }else if(receiverWallet == null){
            log.info("Receiver wallet is not present");
            status = "FAILED";
            statusMsg = "Receiver wallet does not present";
        }else if(amount > senderWallet.getBalance()){
            log.info("Sender does not have sufficient balance");
            status = "FAILED";
            statusMsg = "Sender does not have sufficient balance";
        }else{
            log.info("Sender does not have sufficient successful");
            status = "SUCCESSFUL";
            statusMsg = "transaction is successful";
            updateWallets(senderWallet, receiverWallet, amount);
            log.info("wallets updated");
        }
    }

    @Transactional
    public void updateWallets(Wallet senderWallet, Wallet receiverWallet, Double amount){
        walletRepository.updateWallet(senderWallet.getPhoneNum(), -amount);
        walletRepository.updateWallet(receiverWallet.getPhoneNum(), amount);
    }
}
