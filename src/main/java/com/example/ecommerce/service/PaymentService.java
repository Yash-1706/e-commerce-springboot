package com.example.ecommerce.service;

import com.example.ecommerce.dto.PaymentRequest;
import com.example.ecommerce.dto.PaymentResponse;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for payment operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final RestTemplate restTemplate;
    
    @Value("${payment.service.url}")
    private String paymentServiceUrl;
    
    /**
     * Create payment for an order.
     */
    public PaymentResponse createPayment(PaymentRequest request) {
        log.info("Creating payment for order: {}", request.getOrderId());
        
        // Validate order exists and is in CREATED status
        Order order = orderService.getOrderById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + request.getOrderId()));
        
        if (!"CREATED".equals(order.getStatus())) {
            throw new RuntimeException("Order is not in CREATED status. Current status: " + order.getStatus());
        }
        
        // Check if payment already exists
        Optional<Payment> existingPayment = paymentRepository.findByOrderId(request.getOrderId());
        if (existingPayment.isPresent()) {
            throw new RuntimeException("Payment already exists for order: " + request.getOrderId());
        }
        
        // Create payment record
        String paymentId = "pay_" + UUID.randomUUID().toString().substring(0, 8);
        
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .status("PENDING")
                .paymentId(paymentId)
                .createdAt(Instant.now())
                .build();
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // Call mock payment service
        try {
            callMockPaymentService(savedPayment);
        } catch (Exception e) {
            log.warn("Could not reach payment service: {}. Payment will be processed manually.", e.getMessage());
        }
        
        return PaymentResponse.builder()
                .paymentId(savedPayment.getPaymentId())
                .orderId(savedPayment.getOrderId())
                .amount(savedPayment.getAmount())
                .status(savedPayment.getStatus())
                .build();
    }
    
    /**
     * Call mock payment service.
     */
    private void callMockPaymentService(Payment payment) {
        log.info("Calling mock payment service for payment: {}", payment.getPaymentId());
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("paymentId", payment.getPaymentId());
        requestBody.put("orderId", payment.getOrderId());
        requestBody.put("amount", payment.getAmount());
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    paymentServiceUrl + "/payments/create",
                    requestBody,
                    Map.class
            );
            log.info("Mock payment service response: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("Error calling mock payment service: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Update payment status from webhook.
     */
    public void updatePaymentStatus(String paymentId, String status) {
        log.info("Updating payment {} status to: {}", paymentId, status);
        
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        
        payment.setStatus(status);
        paymentRepository.save(payment);
        
        // Update order status based on payment status
        String orderStatus = "SUCCESS".equals(status) ? "PAID" : "FAILED";
        orderService.updateOrderStatus(payment.getOrderId(), orderStatus);
    }
    
    /**
     * Get payment by order ID.
     */
    public Optional<Payment> getPaymentByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}
