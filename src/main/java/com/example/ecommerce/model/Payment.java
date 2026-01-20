package com.example.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Payment entity representing payment information for an order.
 * Status can be: PENDING, SUCCESS, FAILED
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
    
    @Id
    private String id;
    
    private String orderId;
    
    private Double amount;
    
    private String status;
    
    private String paymentId;
    
    private Instant createdAt;
}
