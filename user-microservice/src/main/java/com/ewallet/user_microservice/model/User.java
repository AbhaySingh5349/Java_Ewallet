package com.ewallet.user_microservice.model;

import com.ewallet.user_microservice.enums.UserIdentificationType;
import com.ewallet.user_microservice.enums.UserStatus;
import com.ewallet.user_microservice.enums.UserType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

// UserDetails lets us provide security using custom user details service

@Data // getters n setters
@AllArgsConstructor
@NoArgsConstructor
@Builder // helps in creating instance
@Entity // telling hibernate that table will exist in DB
@FieldDefaults(level = AccessLevel.PRIVATE) // all non-static fields will have "private" attached
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(length = 30)
    String name;

    @Column(unique = true, length = 50)
    String email;

    @Column(unique = true, nullable = false, length = 11)
    String phoneNum;

    String password;

    @Enumerated(value = EnumType.STRING)
    UserType userType;

    @Enumerated(value = EnumType.STRING)
    UserStatus userStatus;

    String authorities;

    @Enumerated(value = EnumType.STRING)
    UserIdentificationType userIdentificationType;

    String userIdentificationValue;

    // timestamp of location where DB instance is running
    @CreationTimestamp(source = SourceType.DB)
    Date createdOn;

    @UpdateTimestamp
    Date updatedOn;

    // methods from UserDetails interface to uniquely identify user
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(authorities.split(",")).
                map(authority -> new SimpleGrantedAuthority(authority)).
                collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return phoneNum;
    }
}
