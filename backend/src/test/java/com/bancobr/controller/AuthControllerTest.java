package com.bancobr.controller;

import com.bancobr.dto.LoginRequest;
import com.bancobr.dto.LoginResponse;
import com.bancobr.model.Usuario;
import com.bancobr.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para AuthController
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper;
    private Usuario usuario;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        UUID usuarioId = UUID.randomUUID();
        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setSenhaHash("senha_criptografada");
        usuario.setAtivo(true);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("joao@email.com");
        loginRequest.setSenha("senha123");

        loginResponse = LoginResponse.builder()
                .token("jwt_token_exemplo")
                .refreshToken("refresh_token_exemplo")
                .userId(usuarioId.toString())
                .email("joao@email.com")
                .nome("João Silva")
                .tipo("CLIENTE")
                .build();
    }

    @Test
    void login_ComCredenciaisValidas_DeveRetornarToken() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt_token_exemplo"))
                .andExpect(jsonPath("$.userId").value(usuario.getId().toString()))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void login_ComCredenciaisInvalidas_DeveRetornar401() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("Credenciais inválidas"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_ComEmailInvalido_DeveRetornar400() throws Exception {
        // Arrange
        loginRequest.setEmail("email_invalido");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ComSenhaVazia_DeveRetornar400() throws Exception {
        // Arrange
        loginRequest.setSenha("");

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ComDadosNulos_DeveRetornar400() throws Exception {
        // Arrange
        loginRequest.setEmail(null);
        loginRequest.setSenha(null);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refreshToken_ComTokenValido_DeveRetornarNovoToken() throws Exception {
        // Arrange
        String tokenAtual = "token_atual";
        LoginResponse novoResponse = LoginResponse.builder()
                .token("novo_jwt_token")
                .refreshToken("novo_refresh_token")
                .userId(usuario.getId().toString())
                .email("joao@email.com")
                .nome("João Silva")
                .tipo("CLIENTE")
                .build();
        
        when(authService.refreshToken(any(String.class))).thenReturn(novoResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + tokenAtual))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("novo_jwt_token"))
                .andExpect(jsonPath("$.userId").value(usuario.getId().toString()));
    }

    @Test
    void refreshToken_ComTokenInvalido_DeveRetornar401() throws Exception {
        // Arrange
        String tokenInvalido = "token_invalido";
        when(authService.refreshToken(any(String.class)))
                .thenThrow(new RuntimeException("Token inválido"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + tokenInvalido))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshToken_SemToken_DeveRetornar401() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_DeveRetornar200() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout realizado com sucesso"));
    }

    @Test
    void validarToken_ComTokenValido_DeveRetornar200() throws Exception {
        // Arrange
        String tokenValido = "token_valido";
        when(authService.isTokenValid(any(String.class))).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/auth/validar")
                .header("Authorization", "Bearer " + tokenValido))
                .andExpect(status().isOk())
                .andExpect(content().string("Token válido"));
    }

    @Test
    void validarToken_ComTokenInvalido_DeveRetornar401() throws Exception {
        // Arrange
        String tokenInvalido = "token_invalido";
        when(authService.isTokenValid(any(String.class))).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/auth/validar")
                .header("Authorization", "Bearer " + tokenInvalido))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validarToken_SemToken_DeveRetornar401() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/auth/validar"))
                .andExpect(status().isUnauthorized());
    }
} 