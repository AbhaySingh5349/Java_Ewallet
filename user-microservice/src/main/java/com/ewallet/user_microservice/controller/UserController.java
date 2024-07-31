package com.ewallet.user_microservice.controller;

import com.ewallet.user_microservice.dto.CreateUserRequest;
import com.ewallet.user_microservice.model.User;
import com.ewallet.user_microservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public User createUser(@RequestBody @Valid CreateUserRequest userRequest){
        return userService.createUser(userRequest);
    }

    @GetMapping("/user")
    public User getUser(@RequestParam("phoneNum") String phoneNum){
        return userService.getUserByPhoneNum(phoneNum);
    }
}
