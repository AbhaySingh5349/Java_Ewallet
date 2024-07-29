package com.ewallet.user_microservice.mapper;

import com.ewallet.user_microservice.dto.CreateUserRequest;
import com.ewallet.user_microservice.enums.UserStatus;
import com.ewallet.user_microservice.model.User;
import lombok.experimental.UtilityClass;

// automatically inserts "private constructor" & methods as "static" so that we don't have to create its instance

@UtilityClass
public class UserMapper {
    public User mapToUser(CreateUserRequest request){
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
//                .password(request.getPassword()) // since we need to encrypt password & we have Bean of PasswordEncoder, we can autowire it in user service
                .phoneNum(request.getPhoneNum())
                .userIdentificationType(request.getUserIdentificationType())
                .userIdentificationValue(request.getUserIdentificationValue())
                .userStatus(UserStatus.ACTIVE).build();
    }
}
