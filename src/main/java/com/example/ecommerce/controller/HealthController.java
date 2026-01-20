package com.example.ecommerce.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Health check controller for monitoring.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class HealthController {
    
    /**
     * Health check endpoint.
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.info("GET /api/health - Health check");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "ecommerce-api",
                "timestamp", Instant.now().toString()
        ));
    }
}
