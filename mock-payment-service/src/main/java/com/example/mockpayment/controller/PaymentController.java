package com.example.mockpayment.controller;

import com.example.mockpayment.dto.PaymentRequest;
import com.example.mockpayment.dto.PaymentResponse;
import com.example.mockpayment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for mock payment operations.
 */
@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    /**
     * Create a new payment.
     * POST /payments/create
     */
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        log.info("POST /payments/create - PaymentId: {}", request.getPaymentId());
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get payment status.
     * GET /payments/{paymentId}/status
     */
    @GetMapping("/{paymentId}/status")
    public ResponseEntity<Map<String, String>> getPaymentStatus(@PathVariable String paymentId) {
        log.info("GET /payments/{}/status", paymentId);
        String status = paymentService.getPaymentStatus(paymentId);
        return ResponseEntity.ok(Map.of(
                "paymentId", paymentId,
                "status", status
        ));
    }
    
    /**
     * Health check endpoint.
     * GET /payments/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "mock-payment-service"
        ));
    }
}
