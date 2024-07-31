package com.ewallet.transaction_microservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenericConfig {

    // 1 bean of object mapper can be used globally
    // this is part of "jackson-bind" dependency which is automatically wrapped by "spring web"
    @Bean
    ObjectMapper objectMapper(){
        return new ObjectMapper(); // conversion between string <--> json (since we need this while writing reading from kafka)
    }
}
