package com.example.polybookbe;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class PolybookBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolybookBeApplication.class, args);
    }
    @Bean
    CommandLineRunner encodePassword(PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println(passwordEncoder.encode("123456"));
        };
    }
}
