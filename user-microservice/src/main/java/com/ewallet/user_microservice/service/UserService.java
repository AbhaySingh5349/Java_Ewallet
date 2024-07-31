package com.ewallet.user_microservice.service;

import com.ewallet.user_microservice.constants.KafkaConstants;
import com.ewallet.user_microservice.constants.UserCreationTopicConstants;
import com.ewallet.user_microservice.dto.CreateUserRequest;
import com.ewallet.user_microservice.enums.UserType;
import com.ewallet.user_microservice.mapper.UserMapper;
import com.ewallet.user_microservice.model.User;
import com.ewallet.user_microservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// UserDetailsService is provided by spring security which we use to override "loadUserByUsername"

@Service
@Slf4j
public class UserService implements UserDetailsService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    KafkaTemplate kafkaTemplate; // properties are mentioned in application.config & its bean will get created at runtime
    ObjectMapper objectMapper;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, KafkaTemplate kafkaTemplate, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public User loadUserByUsername(String phoneNum) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNum(phoneNum);

        if(user == null){
            throw new UsernameNotFoundException(phoneNum.concat(" user not found"));
        }

        return user;
    }

    public User createUser(CreateUserRequest request) {
        User user = UserMapper.mapToUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUserType(UserType.USER);
        user.setAuthorities("USER");

        log.info("user created: {}", user);

        userRepository.save(user);

        log.info("user saved: {}", user);

        // publish data to kafka
        // notification service will require name & email
        // wallet service will require phoneNum, user status

        // creating key-value pairs
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put(UserCreationTopicConstants.NAME, user.getName());
        objectNode.put(UserCreationTopicConstants.EMAIL, user.getEmail());
        objectNode.put(UserCreationTopicConstants.PHONENUM, user.getPhoneNum());
        objectNode.put(UserCreationTopicConstants.USERID, user.getId());

        String kafkaMsg = objectNode.toString();
        kafkaTemplate.send(KafkaConstants.USER_CREATION_TOPIC, kafkaMsg);

        log.info("user saved msg published to kafka: {}", kafkaMsg);

        return user;
    }

    public User getUserByPhoneNum(String phoneNum) {
        return userRepository.findByPhoneNum(phoneNum);
    }
}
