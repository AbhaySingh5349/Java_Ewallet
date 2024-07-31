package com.ewallet.transaction_microservice.constants;

// since by default all fields are "public static final" in interface, so we used them
// else we could have used class also and in front of each field add "public static final"

public interface KafkaConstants {
    String TRANSACTION_INITIATED_TOPIC = "wallet_transaction_initiated";
}
