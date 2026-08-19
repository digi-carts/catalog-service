package com.digicart.catalog.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Liveness endpoint used by Cloud Run and operators for <em>catalog-service</em>.
 * GET /health and GET /api/health return the same JSON body.
 */
@RestController
public class HealthController {

    @GetMapping(path = {"/health", "/api/health"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "catalog-service"));
    }
}
