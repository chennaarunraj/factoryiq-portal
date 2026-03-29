package com.portal.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/program")
    public ResponseEntity<Map<String, Object>> programFallback() {
        return fallbackResponse("Program Service");
    }

    @GetMapping("/production")
    public ResponseEntity<Map<String, Object>> productionFallback() {
        return fallbackResponse("Production Service");
    }

    @GetMapping("/quality")
    public ResponseEntity<Map<String, Object>> qualityFallback() {
        return fallbackResponse("Quality Service");
    }

    @GetMapping("/supply-chain")
    public ResponseEntity<Map<String, Object>> supplyChainFallback() {
        return fallbackResponse("Supply Chain Service");
    }

    @GetMapping("/after-sales")
    public ResponseEntity<Map<String, Object>> afterSalesFallback() {
        return fallbackResponse("After Sales Service");
    }

    @GetMapping("/documents")
    public ResponseEntity<Map<String, Object>> documentsFallback() {
        return fallbackResponse("Document Service");
    }

    private ResponseEntity<Map<String, Object>> fallbackResponse(String service) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
            "status", 503,
            "message", service + " is currently unavailable. Please try again later.",
            "timestamp", OffsetDateTime.now().toString()
        ));
    }
}