package com.example.ecommerce.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderItem entity representing a product within an order.
 * Price is captured at order time to handle price changes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    private String id;
    
    private String orderId;
    
    private String productId;
    
    private Integer quantity;
    
    private Double price;
}
