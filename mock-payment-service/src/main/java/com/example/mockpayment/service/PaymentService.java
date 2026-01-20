package com.example.mockpayment.service;

import com.example.mockpayment.dto.PaymentRequest;
import com.example.mockpayment.dto.PaymentResponse;
import com.example.mockpayment.dto.WebhookPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for simulating payment processing.
 * Processes payments asynchronously and sends webhook callbacks.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final RestTemplate restTemplate;
    
    @Value("${ecommerce.webhook.url}")
    private String webhookUrl;
    
    @Value("${payment.processing.delay}")
    private int processingDelay;
    
    // In-memory storage for payment status
    private final ConcurrentHashMap<String, String> paymentStatus = new ConcurrentHashMap<>();
    
    /**
     * Create and process a payment asynchronously.
     */
    public PaymentResponse createPayment(PaymentRequest request) {
        log.info("Received payment request - PaymentId: {}, OrderId: {}, Amount: {}", 
                request.getPaymentId(), request.getOrderId(), request.getAmount());
        
        // Store initial status
        paymentStatus.put(request.getPaymentId(), "PENDING");
        
        // Process payment asynchronously
        processPaymentAsync(request);
        
        return PaymentResponse.builder()
                .paymentId(request.getPaymentId())
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .status("PENDING")
                .message("Payment is being processed. Webhook will be sent upon completion.")
                .build();
    }
    
    /**
     * Process payment asynchronously after a delay.
     */
    @Async("taskExecutor")
    public void processPaymentAsync(PaymentRequest request) {
        try {
            log.info("Processing payment: {} - Waiting {} ms", request.getPaymentId(), processingDelay);
            Thread.sleep(processingDelay);
            
            // Simulate payment success (95% success rate)
            String status = new Random().nextInt(100) < 95 ? "SUCCESS" : "FAILED";
            paymentStatus.put(request.getPaymentId(), status);
            
            log.info("Payment {} processed with status: {}", request.getPaymentId(), status);
            
            // Send webhook callback
            sendWebhook(request, status);
            
        } catch (InterruptedException e) {
            log.error("Payment processing interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Send webhook callback to e-commerce API.
     */
    private void sendWebhook(PaymentRequest request, String status) {
        WebhookPayload payload = WebhookPayload.builder()
                .paymentId(request.getPaymentId())
                .orderId(request.getOrderId())
                .status(status)
                .amount(request.getAmount())
                .build();
        
        try {
            log.info("Sending webhook to: {} with status: {}", webhookUrl, status);
            restTemplate.postForEntity(webhookUrl, payload, String.class);
            log.info("Webhook sent successfully for payment: {}", request.getPaymentId());
        } catch (Exception e) {
            log.error("Failed to send webhook: {}", e.getMessage());
        }
    }
    
    /**
     * Get payment status.
     */
    public String getPaymentStatus(String paymentId) {
        return paymentStatus.getOrDefault(paymentId, "NOT_FOUND");
    }
}
