package com.example.mockpayment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Mock Payment Service Application.
 * Simulates payment processing with webhook callbacks.
 */
@SpringBootApplication
@EnableAsync
public class MockPaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockPaymentServiceApplication.class, args);
    }
}
