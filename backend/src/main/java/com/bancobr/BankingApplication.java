package com.bancobr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Classe principal do Sistema Bancário - Banco do Brasil
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
@EnableAsync
public class BankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
        System.out.println("🚀 Sistema Bancário iniciado com sucesso!");
        System.out.println("📊 API disponível em: http://localhost:8080/api");
        System.out.println("🔍 Health Check: http://localhost:8080/api/actuator/health");
    }
} 