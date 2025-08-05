package com.bancobr.dto;

/**
 * DTO para resposta de login
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
public class LoginResponse {
    private String token;
    private String refreshToken;
    private String userId;
    private String email;
    private String nome;
    private String tipo;

    // Construtores
    public LoginResponse() {}

    public LoginResponse(String token, String refreshToken, String userId, String email, String nome, String tipo) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.email = email;
        this.nome = nome;
        this.tipo = tipo;
    }

    // Builder pattern manual
    public static LoginResponseBuilder builder() {
        return new LoginResponseBuilder();
    }

    public static class LoginResponseBuilder {
        private String token;
        private String refreshToken;
        private String userId;
        private String email;
        private String nome;
        private String tipo;

        public LoginResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public LoginResponseBuilder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public LoginResponseBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public LoginResponseBuilder email(String email) {
            this.email = email;
            return this;
        }

        public LoginResponseBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public LoginResponseBuilder tipo(String tipo) {
            this.tipo = tipo;
            return this;
        }

        public LoginResponse build() {
            return new LoginResponse(token, refreshToken, userId, email, nome, tipo);
        }
    }

    // Getters e Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
} 