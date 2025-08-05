package com.bancobr.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller simples para endpoints básicos
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@RestController
public class SimpleController {

    /**
     * Endpoint para manifest.json
     */
    @GetMapping("/manifest.json")
    public ResponseEntity<Map<String, Object>> getManifest() {
        Map<String, Object> manifest = Map.of(
            "name", "Sistema Bancário",
            "short_name", "BancoBR",
            "description", "Sistema de transações bancárias",
            "start_url", "/",
            "display", "standalone",
            "background_color", "#ffffff",
            "theme_color", "#000000",
            "icons", new Object[]{}
        );
        return ResponseEntity.ok(manifest);
    }

    /**
     * Endpoint de health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
} 