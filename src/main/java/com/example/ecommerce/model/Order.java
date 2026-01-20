package com.example.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Order entity representing a customer's order.
 * Status can be: CREATED, PAID, FAILED, CANCELLED
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
    
    @Id
    private String id;
    
    private String userId;
    
    private Double totalAmount;
    
    private String status;
    
    private Instant createdAt;
    
    private List<OrderItem> items;
}
