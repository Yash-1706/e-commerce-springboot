package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateOrderRequest;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for order operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final CartService cartService;
    private final ProductService productService;
    
    /**
     * Create order from user's cart.
     */
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        log.info("Creating order for user: {}", request.getUserId());
        
        // Get cart items
        List<CartItem> cartItems = cartService.getCartItems(request.getUserId());
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot create order.");
        }
        
        // Validate stock and calculate total
        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));
            
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            // Create order item
            OrderItem orderItem = OrderItem.builder()
                    .id(UUID.randomUUID().toString())
                    .productId(cartItem.getProductId())
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .build();
            
            orderItems.add(orderItem);
            totalAmount += product.getPrice() * cartItem.getQuantity();
            
            // Update stock
            productService.updateStock(product.getId(), -cartItem.getQuantity());
        }
        
        // Create order
        Order order = Order.builder()
                .userId(request.getUserId())
                .totalAmount(totalAmount)
                .status("CREATED")
                .createdAt(Instant.now())
                .items(orderItems)
                .build();
        
        Order savedOrder = orderRepository.save(order);
        
        // Update order items with order ID
        savedOrder.getItems().forEach(item -> item.setOrderId(savedOrder.getId()));
        orderRepository.save(savedOrder);
        
        // Clear cart
        cartService.clearCart(request.getUserId());
        
        log.info("Order created: {} with total: {}", savedOrder.getId(), totalAmount);
        return savedOrder;
    }
    
    /**
     * Get order by ID.
     */
    public Optional<Order> getOrderById(String orderId) {
        log.info("Fetching order: {}", orderId);
        return orderRepository.findById(orderId);
    }
    
    /**
     * Get order response with payment details.
     */
    public Optional<OrderResponse> getOrderResponse(String orderId) {
        return orderRepository.findById(orderId)
                .map(this::toOrderResponse);
    }
    
    /**
     * Update order status.
     */
    public void updateOrderStatus(String orderId, String status) {
        log.info("Updating order {} status to: {}", orderId, status);
        orderRepository.findById(orderId).ifPresent(order -> {
            order.setStatus(status);
            orderRepository.save(order);
        });
    }
    
    /**
     * Get orders by user ID.
     */
    public List<Order> getOrdersByUserId(String userId) {
        log.info("Fetching orders for user: {}", userId);
        return orderRepository.findByUserId(userId);
    }
    
    /**
     * Convert Order to OrderResponse with payment details.
     */
    private OrderResponse toOrderResponse(Order order) {
        OrderResponse.PaymentInfo paymentInfo = paymentRepository.findByOrderId(order.getId())
                .map(payment -> OrderResponse.PaymentInfo.builder()
                        .id(payment.getId())
                        .status(payment.getStatus())
                        .amount(payment.getAmount())
                        .paymentId(payment.getPaymentId())
                        .build())
                .orElse(null);
        
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(order.getItems())
                .payment(paymentInfo)
                .build();
    }
    
    /**
     * Cancel an order if not yet paid.
     */
    @Transactional
    public void cancelOrder(String orderId) {
        log.info("Cancelling order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        if (!"CREATED".equals(order.getStatus())) {
            throw new RuntimeException("Cannot cancel order. Current status: " + order.getStatus());
        }
        
        // Restore stock for all items
        for (OrderItem item : order.getItems()) {
            productService.updateStock(item.getProductId(), item.getQuantity());
        }
        
        order.setStatus("CANCELLED");
        orderRepository.save(order);
        log.info("Order {} cancelled successfully", orderId);
    }
}
