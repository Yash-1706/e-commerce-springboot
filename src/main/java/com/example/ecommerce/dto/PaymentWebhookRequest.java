package com.example.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Webhook request DTO for payment callbacks from payment gateway.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWebhookRequest {
    
    private String orderId;
    
    private String paymentId;
    
    private String status;
    
    private Double amount;
}
