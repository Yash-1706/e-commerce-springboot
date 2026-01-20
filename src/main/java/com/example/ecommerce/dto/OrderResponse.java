package com.example.ecommerce.dto;

import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for order details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private String id;
    
    private String userId;
    
    private Double totalAmount;
    
    private String status;
    
    private Instant createdAt;
    
    private List<OrderItem> items;
    
    private PaymentInfo payment;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInfo {
        private String id;
        private String status;
        private Double amount;
        private String paymentId;
    }
}
