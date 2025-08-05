package com.bancobr.controller;

import com.bancobr.dto.LoginRequest;
import com.bancobr.dto.LoginResponse;
import com.bancobr.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador para autenticação
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint para login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Endpoint para refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null) {
                Map<String, Object> error = Map.of("error", "Refresh token é obrigatório");
                return ResponseEntity.badRequest().body(error);
            }

            LoginResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Endpoint para validar token (POST)
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null) {
                Map<String, Object> error = Map.of("error", "Token é obrigatório");
                return ResponseEntity.badRequest().body(error);
            }

            boolean isValid = authService.isTokenValid(token);
            Map<String, Object> response = Map.of("valid", isValid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Endpoint para validar token (GET) - usado pelo frontend
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateTokenGet(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, Object> error = Map.of("error", "Token de autorização inválido");
                return ResponseEntity.badRequest().body(error);
            }

            String token = authHeader.substring(7);
            boolean isValid = authService.isTokenValid(token);
            Map<String, Object> response = Map.of("valid", isValid);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Endpoint para extrair informações do token
     */
    @PostMapping("/info")
    public ResponseEntity<?> getTokenInfo(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            if (token == null) {
                Map<String, Object> error = Map.of("error", "Token é obrigatório");
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, String> info = authService.extractTokenInfo(token);
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Endpoint para logout (stateless - apenas para compatibilidade)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, Object> response = Map.of("message", "Logout realizado com sucesso");
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de health check
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = Map.of(
                "status", "UP",
                "service", "Auth Service",
                "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(response);
    }
} 