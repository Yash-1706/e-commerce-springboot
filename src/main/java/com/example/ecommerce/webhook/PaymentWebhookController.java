package com.example.ecommerce.webhook;

import com.example.ecommerce.dto.PaymentWebhookRequest;
import com.example.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Webhook controller for receiving payment callbacks.
 */
@Slf4j
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class PaymentWebhookController {
    
    private final PaymentService paymentService;
    
    /**
     * Receive payment webhook callback.
     * POST /api/webhooks/payment
     */
    @PostMapping("/payment")
    public ResponseEntity<Map<String, String>> handlePaymentWebhook(@RequestBody PaymentWebhookRequest request) {
        log.info("POST /api/webhooks/payment - Payment: {}, Status: {}", 
                request.getPaymentId(), request.getStatus());
        
        try {
            paymentService.updatePaymentStatus(request.getPaymentId(), request.getStatus());
            log.info("Webhook processed successfully for payment: {}", request.getPaymentId());
            return ResponseEntity.ok(Map.of(
                    "message", "Webhook processed successfully",
                    "paymentId", request.getPaymentId()
            ));
        } catch (RuntimeException e) {
            log.error("Error processing webhook: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }
}
