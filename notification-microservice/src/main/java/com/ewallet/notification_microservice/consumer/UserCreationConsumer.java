package com.ewallet.notification_microservice.consumer;

import com.ewallet.notification_microservice.constants.KafkaConstants;
import com.ewallet.notification_microservice.constants.UserCreationTopicConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

// we can use either of "@Service or @Component"
// only 1 consumer from a particular "groupId" will consume msg

@Component
@Slf4j
public class UserCreationConsumer {
    ObjectMapper objectMapper;
    JavaMailSender javaMailSender; // dependency from spring starter

    @Autowired
    public UserCreationConsumer(ObjectMapper objectMapper, JavaMailSender javaMailSender) {
        this.objectMapper = objectMapper;
        this.javaMailSender = javaMailSender;
    }

    @KafkaListener(topics = KafkaConstants.USER_CREATION_TOPIC, groupId = "notification-group")
    public void userCreated(String msg){
        log.info("user created msg consumed by kafka: {}", msg);

        try {
            ObjectNode objectNode = objectMapper.readValue(msg, ObjectNode.class);

            String name = String.valueOf(objectNode.get(UserCreationTopicConstants.NAME));
            String email = String.valueOf(objectNode.get(UserCreationTopicConstants.EMAIL));

            // dependency from spring starter
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            simpleMailMessage.setFrom("java_ewalletservice@gmail.com");
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("Welcome To Java E-wallet");
            simpleMailMessage.setText("Hey, " + name + "! Welcome To Java E-wallet");

            javaMailSender.send(simpleMailMessage);

            log.info("User creation mail sent: {}", simpleMailMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
