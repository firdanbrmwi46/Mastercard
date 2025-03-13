package com.mastercard.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "d1k4@passw0rd";
        String hashedPassword = encoder.encode(rawPassword);
        System.out.println(hashedPassword);
    }
}