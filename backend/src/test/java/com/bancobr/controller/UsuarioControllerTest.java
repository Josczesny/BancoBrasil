package com.bancobr.controller;

import com.bancobr.model.Usuario;
import com.bancobr.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para UsuarioController
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    private ObjectMapper objectMapper;
    private Usuario usuario;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        
        usuarioId = UUID.randomUUID();
        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setCpf("12345678901");
        usuario.setSenhaHash("senha_criptografada");
        usuario.setTipo(Usuario.TipoUsuario.CLIENTE);
        usuario.setAtivo(true);
        usuario.setCriadoEm(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarTodos_ComAdmin_DeveRetornarLista() throws Exception {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioService.listarTodos()).thenReturn(usuarios);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(usuarioId.toString()))
                .andExpect(jsonPath("$[0].nome").value("João Silva"))
                .andExpect(jsonPath("$[0].email").value("joao@email.com"));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void listarTodos_ComCliente_DeveRetornar403() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarTodos_SemAutenticacao_DeveRetornar401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorId_ComIdValido_DeveRetornarUsuario() throws Exception {
        // Arrange
        when(usuarioService.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/{id}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId.toString()))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorId_ComIdInexistente_DeveRetornar404() throws Exception {
        // Arrange
        when(usuarioService.buscarPorId(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/{id}", usuarioId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarUsuario_ComDadosValidos_DeveCriarUsuario() throws Exception {
        // Arrange
        when(usuarioService.criarUsuario(any(Usuario.class))).thenReturn(usuario);

        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(usuarioId.toString()))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarUsuario_ComDadosInvalidos_DeveRetornar400() throws Exception {
        // Arrange
        usuario.setEmail("email_invalido");

        // Act & Assert
        mockMvc.perform(post("/api/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarUsuario_ComDadosValidos_DeveAtualizarUsuario() throws Exception {
        // Arrange
        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setNome("João Silva Atualizado");
        usuarioAtualizado.setEmail("joao.novo@email.com");
        
        when(usuarioService.atualizarUsuario(eq(usuarioId), any(Usuario.class))).thenReturn(usuario);

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/{id}", usuarioId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuarioAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarUsuario_ComUsuarioInexistente_DeveRetornar404() throws Exception {
        // Arrange
        when(usuarioService.atualizarUsuario(eq(usuarioId), any(Usuario.class)))
                .thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        mockMvc.perform(put("/api/usuarios/{id}", usuarioId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void alterarStatus_ComStatusValido_DeveAlterarStatus() throws Exception {
        // Arrange
        when(usuarioService.alterarStatus(usuarioId, false)).thenReturn(usuario);

        // Act & Assert
        mockMvc.perform(patch("/api/usuarios/{id}/status", usuarioId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ativo\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void alterarStatus_ComUsuarioInexistente_DeveRetornar404() throws Exception {
        // Arrange
        when(usuarioService.alterarStatus(usuarioId, false))
                .thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        mockMvc.perform(patch("/api/usuarios/{id}/status", usuarioId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ativo\": false}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removerUsuario_ComUsuarioValido_DeveRemoverUsuario() throws Exception {
        // Arrange
        when(usuarioService.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));

        // Act & Assert
        mockMvc.perform(delete("/api/usuarios/{id}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuário removido com sucesso"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removerUsuario_ComUsuarioInexistente_DeveRetornar404() throws Exception {
        // Arrange
        when(usuarioService.buscarPorId(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/api/usuarios/{id}", usuarioId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarPorTipo_ComTipoValido_DeveRetornarLista() throws Exception {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuario);
        when(usuarioService.listarPorTipo(Usuario.TipoUsuario.CLIENTE)).thenReturn(usuarios);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/tipo/CLIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(usuarioId.toString()))
                .andExpect(jsonPath("$[0].nome").value("João Silva"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorEmail_ComEmailValido_DeveRetornarUsuario() throws Exception {
        // Arrange
        when(usuarioService.buscarPorEmail("joao@email.com")).thenReturn(Optional.of(usuario));

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/email/joao@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId.toString()))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorEmail_ComEmailInexistente_DeveRetornar404() throws Exception {
        // Arrange
        when(usuarioService.buscarPorEmail("inexistente@email.com")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/email/inexistente@email.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void verificarEmail_ComEmailExistente_DeveRetornarTrue() throws Exception {
        // Arrange
        when(usuarioService.emailExiste("joao@email.com")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/verificar-email/joao@email.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void verificarEmail_ComEmailInexistente_DeveRetornarFalse() throws Exception {
        // Arrange
        when(usuarioService.emailExiste("inexistente@email.com")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/verificar-email/inexistente@email.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void verificarCpf_ComCpfExistente_DeveRetornarTrue() throws Exception {
        // Arrange
        when(usuarioService.cpfExiste("12345678901")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/verificar-cpf/12345678901"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void verificarCpf_ComCpfInexistente_DeveRetornarFalse() throws Exception {
        // Arrange
        when(usuarioService.cpfExiste("00000000000")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/usuarios/verificar-cpf/00000000000"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
} 