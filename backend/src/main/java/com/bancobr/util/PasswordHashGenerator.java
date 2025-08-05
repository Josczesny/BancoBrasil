package com.bancobr.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilitário para gerar hashes de senha
 * Usado apenas para testes e configuração inicial
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String senha = "admin123";
        String hash = encoder.encode(senha);
        
        System.out.println("Senha: " + senha);
        System.out.println("Hash: " + hash);
        
        // Verifica se o hash está correto
        boolean matches = encoder.matches(senha, hash);
        System.out.println("Hash válido: " + matches);
    }
} 