package com.bancobr.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.security.Key;

/**
 * Configuração para JWT
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@Configuration
public class JwtConfig {

    /**
     * Bean para chave secreta JWT segura
     * 
     * @return SecretKey configurada para HS512
     */
    @Bean
    public SecretKey jwtSecretKey() {
        return Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
    }
} 