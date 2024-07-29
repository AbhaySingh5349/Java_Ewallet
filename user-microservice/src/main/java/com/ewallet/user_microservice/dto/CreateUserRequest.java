package com.ewallet.user_microservice.dto;

import com.ewallet.user_microservice.enums.UserIdentificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE) // all non-static fields will have "private" attached
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    String name;

    String email;

    @NotBlank
    String phoneNum;

    @NotBlank
    String password;

    @NotNull
    UserIdentificationType userIdentificationType;

    @NotNull
    String userIdentificationValue;
}
