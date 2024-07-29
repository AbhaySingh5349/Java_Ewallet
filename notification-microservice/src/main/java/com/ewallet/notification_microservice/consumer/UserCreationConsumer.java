package com.ewallet.notification_microservice.consumer;

import com.ewallet.notification_microservice.constants.KafkaConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// we can use either of "@Service or @Component"
// only 1 consumer from a particular "groupId" will consume msg

@Component
@Slf4j
public class UserCreationConsumer {
    @KafkaListener(topics = KafkaConstants.USER_CREATION_TOPIC, groupId = "notification-group")
    public void userCreated(String msg){
        log.info("user created msg received: {}", msg);
    }
}
