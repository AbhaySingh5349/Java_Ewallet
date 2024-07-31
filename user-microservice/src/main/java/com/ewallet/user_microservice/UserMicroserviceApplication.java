package com.ewallet.user_microservice;

import com.ewallet.user_microservice.enums.UserStatus;
import com.ewallet.user_microservice.enums.UserType;
import com.ewallet.user_microservice.model.User;
import com.ewallet.user_microservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UserMicroserviceApplication implements CommandLineRunner {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(UserMicroserviceApplication.class, args);
	}

	// non-static method (if we want to perform specific task after app startup)
	@Override
	public void run(String... args) throws Exception {
		// creating entry in User table for service-to-service communication for any transaction
		User transactionService = User.builder()
				.phoneNum("txn_service")
				.password(passwordEncoder.encode("txn_service"))
				.userStatus(UserStatus.ACTIVE)
				.userType(UserType.SERVICE)
				.authorities("SERVICE") // for service-to-service communication
				.build();

		// since phone um has unique constraint
		if(userRepository.findByPhoneNum("txn_service") == null){
			userRepository.save(transactionService);
		}
	}
}
