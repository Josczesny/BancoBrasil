package com.bancobr.service;

import com.bancobr.model.Usuario;
import com.bancobr.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para UsuarioService
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LogService logService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioValido;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        usuarioValido = new Usuario();
        usuarioValido.setId(usuarioId);
        usuarioValido.setNome("João Silva");
        usuarioValido.setEmail("joao@email.com");
        usuarioValido.setCpf("12345678901");
        usuarioValido.setSenhaHash("senha123");
        usuarioValido.setTipo(Usuario.TipoUsuario.CLIENTE);
        usuarioValido.setAtivo(true);
        usuarioValido.setCriadoEm(LocalDateTime.now());
    }

    @Test
    void criarUsuario_ComDadosValidos_DeveCriarUsuarioComSucesso() {
        // Arrange
        String senhaCriptografada = "senha_criptografada";
        when(usuarioRepository.existsByEmail(usuarioValido.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByCpf(usuarioValido.getCpf())).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn(senhaCriptografada); // Corrigido para usar a senha correta
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioValido);
        when(logService.criarLog(any(Usuario.class), anyString(), anyString(), any(UUID.class)))
                .thenReturn(null);

        // Act
        Usuario resultado = usuarioService.criarUsuario(usuarioValido);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioId, resultado.getId());
        assertEquals(senhaCriptografada, resultado.getSenhaHash());
        assertTrue(resultado.getAtivo());
        assertNotNull(resultado.getCriadoEm());

        verify(usuarioRepository).existsByEmail(usuarioValido.getEmail());
        verify(usuarioRepository).existsByCpf(usuarioValido.getCpf());
        verify(passwordEncoder).encode("senha123"); // Corrigido para usar string literal
        verify(usuarioRepository).save(usuarioValido);
        verify(logService).criarLog(usuarioValido, "INSERT", "usuarios", usuarioValido.getId());
    }

    @Test
    void criarUsuario_ComEmailDuplicado_DeveLancarExcecao() {
        // Arrange
        when(usuarioRepository.existsByEmail(usuarioValido.getEmail())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> usuarioService.criarUsuario(usuarioValido));
        
        assertEquals("Email já cadastrado", exception.getMessage());
        verify(usuarioRepository).existsByEmail(usuarioValido.getEmail());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void criarUsuario_ComCpfDuplicado_DeveLancarExcecao() {
        // Arrange
        when(usuarioRepository.existsByEmail(usuarioValido.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByCpf(usuarioValido.getCpf())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> usuarioService.criarUsuario(usuarioValido));
        
        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(usuarioRepository).existsByEmail(usuarioValido.getEmail());
        verify(usuarioRepository).existsByCpf(usuarioValido.getCpf());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void validarCredenciais_ComCredenciaisValidas_DeveRetornarUsuario() {
        // Arrange
        String senhaCriptografada = "senha_criptografada";
        usuarioValido.setSenhaHash(senhaCriptografada);
        
        when(usuarioRepository.findByEmailAndAtivoTrue(usuarioValido.getEmail()))
                .thenReturn(Optional.of(usuarioValido));
        when(passwordEncoder.matches("senha123", senhaCriptografada)).thenReturn(true);

        // Act
        Optional<Usuario> resultado = usuarioService.validarCredenciais(usuarioValido.getEmail(), "senha123");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(usuarioValido, resultado.get());
        verify(usuarioRepository).findByEmailAndAtivoTrue(usuarioValido.getEmail());
        verify(passwordEncoder).matches("senha123", senhaCriptografada);
    }

    @Test
    void validarCredenciais_ComSenhaInvalida_DeveRetornarVazio() {
        // Arrange
        String senhaCriptografada = "senha_criptografada";
        usuarioValido.setSenhaHash(senhaCriptografada);
        
        when(usuarioRepository.findByEmailAndAtivoTrue(usuarioValido.getEmail()))
                .thenReturn(Optional.of(usuarioValido));
        when(passwordEncoder.matches("senha_errada", senhaCriptografada)).thenReturn(false);

        // Act
        Optional<Usuario> resultado = usuarioService.validarCredenciais(usuarioValido.getEmail(), "senha_errada");

        // Assert
        assertFalse(resultado.isPresent());
        verify(usuarioRepository).findByEmailAndAtivoTrue(usuarioValido.getEmail());
        verify(passwordEncoder).matches("senha_errada", senhaCriptografada);
    }

    @Test
    void validarCredenciais_ComEmailInexistente_DeveRetornarVazio() {
        // Arrange
        when(usuarioRepository.findByEmailAndAtivoTrue(usuarioValido.getEmail()))
                .thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioService.validarCredenciais(usuarioValido.getEmail(), "senha123");

        // Assert
        assertFalse(resultado.isPresent());
        verify(usuarioRepository).findByEmailAndAtivoTrue(usuarioValido.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void buscarPorId_ComIdValido_DeveRetornarUsuario() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioValido));

        // Act
        Optional<Usuario> resultado = usuarioService.buscarPorId(usuarioId);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(usuarioValido, resultado.get());
        verify(usuarioRepository).findById(usuarioId);
    }

    @Test
    void buscarPorId_ComIdInexistente_DeveRetornarVazio() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioService.buscarPorId(usuarioId);

        // Assert
        assertFalse(resultado.isPresent());
        verify(usuarioRepository).findById(usuarioId);
    }

    @Test
    void buscarPorEmail_ComEmailValido_DeveRetornarUsuario() {
        // Arrange
        when(usuarioRepository.findByEmailAndAtivoTrue(usuarioValido.getEmail()))
                .thenReturn(Optional.of(usuarioValido));

        // Act
        Optional<Usuario> resultado = usuarioService.buscarPorEmail(usuarioValido.getEmail());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(usuarioValido, resultado.get());
        verify(usuarioRepository).findByEmailAndAtivoTrue(usuarioValido.getEmail());
    }

    @Test
    void listarPorTipo_ComTipoValido_DeveRetornarLista() {
        // Arrange
        List<Usuario> usuarios = Arrays.asList(usuarioValido);
        when(usuarioRepository.findByTipoAndAtivoTrue(Usuario.TipoUsuario.CLIENTE))
                .thenReturn(usuarios);

        // Act
        List<Usuario> resultado = usuarioService.listarPorTipo(Usuario.TipoUsuario.CLIENTE);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(usuarioValido, resultado.get(0));
        verify(usuarioRepository).findByTipoAndAtivoTrue(Usuario.TipoUsuario.CLIENTE);
    }

    @Test
    void atualizarUsuario_ComDadosValidos_DeveAtualizarUsuario() {
        // Arrange
        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setNome("João Silva Atualizado");
        usuarioAtualizado.setEmail("joao.novo@email.com");
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioValido));
        when(usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioValido);
        when(logService.criarLog(any(Usuario.class), anyString(), anyString(), any(UUID.class)))
                .thenReturn(null);

        // Act
        Usuario resultado = usuarioService.atualizarUsuario(usuarioId, usuarioAtualizado);

        // Assert
        assertNotNull(resultado);
        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository).existsByEmail(usuarioAtualizado.getEmail());
        verify(usuarioRepository).save(usuarioValido);
        verify(logService).criarLog(usuarioValido, "UPDATE", "usuarios", usuarioValido.getId());
    }

    @Test
    void atualizarUsuario_ComUsuarioInexistente_DeveLancarExcecao() {
        // Arrange
        Usuario usuarioAtualizado = new Usuario();
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> usuarioService.atualizarUsuario(usuarioId, usuarioAtualizado));
        
        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void alterarStatus_ComUsuarioValido_DeveAlterarStatus() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioValido));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioValido);
        when(logService.criarLog(any(Usuario.class), anyString(), anyString(), any(UUID.class)))
                .thenReturn(null);

        // Act
        Usuario resultado = usuarioService.alterarStatus(usuarioId, false);

        // Assert
        assertNotNull(resultado);
        assertFalse(usuarioValido.getAtivo());
        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository).save(usuarioValido);
        verify(logService).criarLog(usuarioValido, "UPDATE", "usuarios", usuarioValido.getId());
    }

    @Test
    void removerUsuario_ComUsuarioValido_DeveDesativarUsuario() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuarioValido));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioValido);
        when(logService.criarLog(any(Usuario.class), anyString(), anyString(), any(UUID.class)))
                .thenReturn(null);

        // Act
        usuarioService.removerUsuario(usuarioId);

        // Assert
        assertFalse(usuarioValido.getAtivo());
        verify(usuarioRepository).findById(usuarioId);
        verify(usuarioRepository).save(usuarioValido);
        verify(logService).criarLog(usuarioValido, "DELETE", "usuarios", usuarioValido.getId());
    }

    @Test
    void emailExiste_ComEmailExistente_DeveRetornarTrue() {
        // Arrange
        when(usuarioRepository.existsByEmail(usuarioValido.getEmail())).thenReturn(true);

        // Act
        boolean resultado = usuarioService.emailExiste(usuarioValido.getEmail());

        // Assert
        assertTrue(resultado);
        verify(usuarioRepository).existsByEmail(usuarioValido.getEmail());
    }

    @Test
    void cpfExiste_ComCpfExistente_DeveRetornarTrue() {
        // Arrange
        when(usuarioRepository.existsByCpf(usuarioValido.getCpf())).thenReturn(true);

        // Act
        boolean resultado = usuarioService.cpfExiste(usuarioValido.getCpf());

        // Assert
        assertTrue(resultado);
        verify(usuarioRepository).existsByCpf(usuarioValido.getCpf());
    }
} 