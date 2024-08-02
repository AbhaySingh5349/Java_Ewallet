package com.ewallet.transaction_microservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

// whenever we want to create our own beans, we need '@Configuration' annotation
// http://localhost:8084/spring-security

@Configuration
public class SecurityConfig {
    // to specify which API can be accessed different authorities (authorization for routes)
    // API should have at least 1 of authorities assigned to users
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,"/transaction").hasAuthority("USER") // users can initiate transaction
                        .requestMatchers(HttpMethod.GET,"/transaction/all").hasAuthority("USER") // ensures authenticated users can fetch transactions initiated by them
                        .anyRequest().permitAll())
                .formLogin(withDefaults()) // we need a login form for accessing authorized routes (browser)
                .httpBasic(withDefaults()) // for postman, we need to provide "Basic Auth"
                .csrf(csrf -> csrf.disable()); // to enable post requests through POSTMAN
        return http.build();
    }

    // password encoding
    @Bean
    public PasswordEncoder getEncoder(){
        return new BCryptPasswordEncoder();
    }
}
