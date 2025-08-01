package com.ingcase.digitalwallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class DigitalWalletApplicationTest {

    public static void main(String[] args) {
        SpringApplication.run(DigitalWalletApplication.class, args);
    }
}
