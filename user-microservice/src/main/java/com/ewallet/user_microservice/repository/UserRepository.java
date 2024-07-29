package com.ewallet.user_microservice.repository;

import com.ewallet.user_microservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByPhoneNum(String phoneNum);
}
