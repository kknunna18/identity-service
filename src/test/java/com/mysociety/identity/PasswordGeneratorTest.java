package com.mysociety.identity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordGeneratorTest {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void generatePassword() {
        System.out.println(
                passwordEncoder.encode("MySociety@123")
        );
    }
}
