package com.ewallet.transaction_microservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE) // all non-static fields will have "private" attached
@AllArgsConstructor
@NoArgsConstructor
public class InitiateTransactionRequest {
    @NotBlank
    String receiverPhoneNum;

    @Positive
    Double amount;

    String purpose;
}
