package com.ewallet.wallet_microservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data // getters n setters
@AllArgsConstructor
@NoArgsConstructor
@Builder // helps in creating instance
@Entity // telling hibernate that table will exist in DB
@FieldDefaults(level = AccessLevel.PRIVATE) // all non-static fields will have "private" attached
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String phoneNum;

    Integer userId;

    Double balance;

    // timestamp of location where DB instance is running
    @CreationTimestamp(source = SourceType.DB)
    Date createdOn;

    @UpdateTimestamp
    Date updatedOn;
}
