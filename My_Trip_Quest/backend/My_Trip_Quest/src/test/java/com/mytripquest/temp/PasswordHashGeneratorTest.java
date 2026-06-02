package com.mytripquest.temp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordHashGeneratorTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void generatePasswordHash() {
        String password = "qwer1234";
        String encodedPassword = passwordEncoder.encode(password);
        System.out.println("==================================================");
        System.out.println("Generated Password Hash for '" + password + "':");
        System.out.println(encodedPassword);
        System.out.println("==================================================");
    }
}
