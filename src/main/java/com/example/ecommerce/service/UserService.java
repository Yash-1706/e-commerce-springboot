package com.example.ecommerce.service;

import com.example.ecommerce.dto.CreateUserRequest;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for user operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    /**
     * Create a new user.
     */
    public User createUser(CreateUserRequest request) {
        log.info("Creating user: {}", request.getUsername());
        
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .role(request.getRole() != null ? request.getRole() : "USER")
                .build();
        
        return userRepository.save(user);
    }
    
    /**
     * Get all users.
     */
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }
    
    /**
     * Get user by ID.
     */
    public Optional<User> getUserById(String id) {
        log.info("Fetching user with ID: {}", id);
        return userRepository.findById(id);
    }
    
    /**
     * Get user by username.
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
