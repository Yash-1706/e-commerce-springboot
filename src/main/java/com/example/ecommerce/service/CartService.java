package com.example.ecommerce.service;

import com.example.ecommerce.dto.AddToCartRequest;
import com.example.ecommerce.dto.CartItemResponse;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for cart operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    
    /**
     * Add item to cart. If item exists, update quantity.
     */
    public CartItem addToCart(AddToCartRequest request) {
        log.info("Adding to cart - User: {}, Product: {}, Qty: {}", 
                request.getUserId(), request.getProductId(), request.getQuantity());
        
        // Check if product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + request.getProductId()));
        
        // Check stock availability
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
        }
        
        // Check if item already in cart
        Optional<CartItem> existingItem = cartItemRepository
                .findByUserIdAndProductId(request.getUserId(), request.getProductId());
        
        if (existingItem.isPresent()) {
            // Update quantity
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            
            if (product.getStock() < newQuantity) {
                throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
            }
            
            cartItem.setQuantity(newQuantity);
            return cartItemRepository.save(cartItem);
        }
        
        // Create new cart item
        CartItem cartItem = CartItem.builder()
                .userId(request.getUserId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .build();
        
        return cartItemRepository.save(cartItem);
    }
    
    /**
     * Get all cart items for a user with product details.
     */
    public List<CartItemResponse> getCart(String userId) {
        log.info("Fetching cart for user: {}", userId);
        
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        
        return cartItems.stream()
                .map(this::toCartItemResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get raw cart items for a user.
     */
    public List<CartItem> getCartItems(String userId) {
        return cartItemRepository.findByUserId(userId);
    }
    
    /**
     * Clear all cart items for a user.
     */
    @Transactional
    public void clearCart(String userId) {
        log.info("Clearing cart for user: {}", userId);
        cartItemRepository.deleteByUserId(userId);
    }
    
    /**
     * Convert CartItem to CartItemResponse with product details.
     */
    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        CartItemResponse.ProductInfo productInfo = productRepository.findById(cartItem.getProductId())
                .map(product -> CartItemResponse.ProductInfo.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .build())
                .orElse(null);
        
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .product(productInfo)
                .build();
    }
}
