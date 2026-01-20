package com.example.mockpayment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Webhook payload DTO sent to e-commerce API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookPayload {
    private String paymentId;
    private String orderId;
    private String status;
    private Double amount;
}
