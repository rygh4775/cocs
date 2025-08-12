package com.cocs.webapp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cocs.server.HectorUtils;

/**
 * Health check controller for load balancer health checks
 * and application monitoring
 */
@Controller
public class HealthController {
    
    @Autowired(required = false)
    private HectorUtils hectorUtils;
    
    /**
     * Basic health check endpoint
     * Returns OK if the application is running
     */
    @RequestMapping(value = "/health", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
    
    /**
     * Detailed health check endpoint
     * Returns application status including database connectivity
     */
    @RequestMapping(value = "/health/detailed", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        
        // Check database connectivity
        Map<String, Object> database = new HashMap<>();
        try {
            if (hectorUtils != null) {
                // Try to perform a simple database operation
                boolean dbConnected = hectorUtils.isConnected();
                database.put("status", dbConnected ? "UP" : "DOWN");
                database.put("type", "Cassandra");
            } else {
                database.put("status", "UNKNOWN");
                database.put("message", "Database connection not initialized");
            }
        } catch (Exception e) {
            database.put("status", "DOWN");
            database.put("error", e.getMessage());
        }
        health.put("database", database);
        
        // Check JVM health
        Map<String, Object> jvm = new HashMap<>();
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        jvm.put("maxMemory", maxMemory);
        jvm.put("totalMemory", totalMemory);
        jvm.put("freeMemory", freeMemory);
        jvm.put("usedMemory", usedMemory);
        jvm.put("memoryUsagePercent", (double) usedMemory / maxMemory * 100);
        
        health.put("jvm", jvm);
        
        // Determine overall status
        boolean isHealthy = "UP".equals(database.get("status")) || "UNKNOWN".equals(database.get("status"));
        
        if (isHealthy) {
            return ResponseEntity.ok(health);
        } else {
            health.put("status", "DOWN");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
        }
    }
    
    /**
     * Readiness check endpoint
     * Returns OK when the application is ready to serve traffic
     */
    @RequestMapping(value = "/ready", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> ready() {
        // Check if critical components are initialized
        try {
            // Add any initialization checks here
            return ResponseEntity.ok("READY");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("NOT_READY");
        }
    }
    
    /**
     * Liveness check endpoint
     * Returns OK if the application is alive (not deadlocked)
     */
    @RequestMapping(value = "/live", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> live() {
        return ResponseEntity.ok("ALIVE");
    }
}