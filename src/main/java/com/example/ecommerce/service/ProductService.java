package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateProductRequest;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for product operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    
    private final ProductRepository productRepository;
    
    /**
     * Create a new product.
     */
    public Product createProduct(CreateProductRequest request) {
        log.info("Creating product: {}", request.getName());
        
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();
        
        return productRepository.save(product);
    }
    
    /**
     * Get all products.
     */
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }
    
    /**
     * Get product by ID.
     */
    public Optional<Product> getProductById(String id) {
        log.info("Fetching product with ID: {}", id);
        return productRepository.findById(id);
    }
    
    /**
     * Update product stock.
     */
    public void updateStock(String productId, int quantityChange) {
        log.info("Updating stock for product {}: {}", productId, quantityChange);
        
        productRepository.findById(productId).ifPresent(product -> {
            product.setStock(product.getStock() + quantityChange);
            productRepository.save(product);
        });
    }
    
    /**
     * Check if product has sufficient stock.
     */
    public boolean hasStock(String productId, int requiredQuantity) {
        return productRepository.findById(productId)
                .map(product -> product.getStock() >= requiredQuantity)
                .orElse(false);
    }
}
