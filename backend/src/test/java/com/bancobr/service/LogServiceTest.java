package com.bancobr.service;

import com.bancobr.model.Log;
import com.bancobr.model.Usuario;
import com.bancobr.repository.LogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para LogService
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private LogService logService;

    private Usuario usuario;
    private Log log;
    private UUID usuarioId;
    private UUID logId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        logId = UUID.randomUUID();

        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setAtivo(true);

        log = new Log();
        log.setId(logId);
        log.setUsuario(usuario);
        log.setAcao("INSERT");
        log.setTabela("usuarios");
        log.setRegistroId(usuarioId);
        log.setCriadoEm(LocalDateTime.now());
        log.setDadosAnteriores("{}");
        log.setDadosNovos("{\"nome\":\"João Silva\"}");
    }

    @Test
    void criarLog_ComDadosValidos_DeveCriarLogComSucesso() {
        // Arrange
        String acao = "INSERT";
        String tabela = "usuarios";
        UUID registroId = UUID.randomUUID();
        
        Log logSalvo = new Log();
        logSalvo.setId(logId);
        logSalvo.setUsuario(usuario);
        logSalvo.setAcao(acao);
        logSalvo.setTabela(tabela);
        logSalvo.setRegistroId(registroId);
        logSalvo.setCriadoEm(LocalDateTime.now());
        
        when(logRepository.save(any(Log.class))).thenReturn(logSalvo);

        // Act
        Log resultado = logService.criarLog(usuario, acao, tabela, registroId);

        // Assert
        assertNotNull(resultado);
        assertEquals(logId, resultado.getId());
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(acao, resultado.getAcao());
        assertEquals(tabela, resultado.getTabela());
        assertEquals(registroId, resultado.getRegistroId());
        assertNotNull(resultado.getCriadoEm());

        verify(logRepository).save(any(Log.class));
    }

    @Test
    void criarLogSistema_ComDadosValidos_DeveCriarLogSistemaComSucesso() {
        // Arrange
        String acao = "TRANSFERENCIA";
        String tabela = "transacoes";
        UUID registroId = UUID.randomUUID();
        
        Log logSistema = new Log();
        logSistema.setId(logId);
        logSistema.setUsuario(null); // Log do sistema não tem usuário
        logSistema.setAcao(acao);
        logSistema.setTabela(tabela);
        logSistema.setRegistroId(registroId);
        logSistema.setCriadoEm(LocalDateTime.now());
        
        when(logRepository.save(any(Log.class))).thenReturn(logSistema);

        // Act
        Log resultado = logService.criarLogSistema(acao, tabela, registroId);

        // Assert
        assertNotNull(resultado);
        assertEquals(logId, resultado.getId());
        assertNull(resultado.getUsuario()); // Log do sistema não tem usuário
        assertEquals(acao, resultado.getAcao());
        assertEquals(tabela, resultado.getTabela());
        assertEquals(registroId, resultado.getRegistroId());
        assertNotNull(resultado.getCriadoEm());

        verify(logRepository).save(any(Log.class));
    }

    @Test
    void criarLogComAuditoria_ComDadosValidos_DeveCriarLogComAuditoria() {
        // Arrange
        String acao = "UPDATE";
        String tabela = "usuarios";
        UUID registroId = UUID.randomUUID();
        String dadosAnteriores = "{\"nome\":\"João\"}";
        String dadosNovos = "{\"nome\":\"João Silva\"}";
        
        Log logAuditoria = new Log();
        logAuditoria.setId(logId);
        logAuditoria.setUsuario(usuario);
        logAuditoria.setAcao(acao);
        logAuditoria.setTabela(tabela);
        logAuditoria.setRegistroId(registroId);
        logAuditoria.setDadosAnteriores(dadosAnteriores);
        logAuditoria.setDadosNovos(dadosNovos);
        logAuditoria.setCriadoEm(LocalDateTime.now());
        
        when(logRepository.save(any(Log.class))).thenReturn(logAuditoria);

        // Act
        Log resultado = logService.criarLogComAuditoria(usuario, acao, tabela, registroId, dadosAnteriores, dadosNovos);

        // Assert
        assertNotNull(resultado);
        assertEquals(logId, resultado.getId());
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(acao, resultado.getAcao());
        assertEquals(tabela, resultado.getTabela());
        assertEquals(registroId, resultado.getRegistroId());
        assertEquals(dadosAnteriores, resultado.getDadosAnteriores());
        assertEquals(dadosNovos, resultado.getDadosNovos());
        assertNotNull(resultado.getCriadoEm());

        verify(logRepository).save(any(Log.class));
    }

    @Test
    void criarLogSistemaComAuditoria_ComDadosValidos_DeveCriarLogSistemaComAuditoria() {
        // Arrange
        String acao = "UPDATE";
        String tabela = "contas";
        UUID registroId = UUID.randomUUID();
        String dadosAnteriores = "{\"saldo\":1000.00}";
        String dadosNovos = "{\"saldo\":1500.00}";
        
        Log logSistemaAuditoria = new Log();
        logSistemaAuditoria.setId(logId);
        logSistemaAuditoria.setUsuario(null); // Log do sistema não tem usuário
        logSistemaAuditoria.setAcao(acao);
        logSistemaAuditoria.setTabela(tabela);
        logSistemaAuditoria.setRegistroId(registroId);
        logSistemaAuditoria.setDadosAnteriores(dadosAnteriores);
        logSistemaAuditoria.setDadosNovos(dadosNovos);
        logSistemaAuditoria.setCriadoEm(LocalDateTime.now());
        
        when(logRepository.save(any(Log.class))).thenReturn(logSistemaAuditoria);

        // Act
        Log resultado = logService.criarLogSistemaComAuditoria(acao, tabela, registroId, dadosAnteriores, dadosNovos);

        // Assert
        assertNotNull(resultado);
        assertEquals(logId, resultado.getId());
        assertNull(resultado.getUsuario()); // Log do sistema não tem usuário
        assertEquals(acao, resultado.getAcao());
        assertEquals(tabela, resultado.getTabela());
        assertEquals(registroId, resultado.getRegistroId());
        assertEquals(dadosAnteriores, resultado.getDadosAnteriores());
        assertEquals(dadosNovos, resultado.getDadosNovos());
        assertNotNull(resultado.getCriadoEm());

        verify(logRepository).save(any(Log.class));
    }

    @Test
    void buscarPorId_ComIdValido_DeveRetornarLog() {
        // Arrange
        when(logRepository.findById(logId)).thenReturn(Optional.of(log));

        // Act
        Optional<Log> resultado = logService.buscarPorId(logId);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(log, resultado.get());
        verify(logRepository).findById(logId);
    }

    @Test
    void buscarPorId_ComIdInexistente_DeveRetornarVazio() {
        // Arrange
        when(logRepository.findById(logId)).thenReturn(Optional.empty());

        // Act
        Optional<Log> resultado = logService.buscarPorId(logId);

        // Assert
        assertFalse(resultado.isPresent());
        verify(logRepository).findById(logId);
    }

    @Test
    void buscarComUsuario_ComIdValido_DeveRetornarLog() {
        // Arrange
        when(logRepository.findByIdWithUsuario(logId)).thenReturn(log);

        // Act
        Log resultado = logService.buscarComUsuario(logId);

        // Assert
        assertNotNull(resultado);
        assertEquals(log, resultado);
        verify(logRepository).findByIdWithUsuario(logId);
    }

    @Test
    void listarPorUsuario_ComUsuarioValido_DeveRetornarLista() {
        // Arrange
        List<Log> logs = Arrays.asList(log);
        when(logRepository.findByUsuarioId(usuarioId)).thenReturn(logs);

        // Act
        List<Log> resultado = logService.listarPorUsuario(usuarioId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(log, resultado.get(0));
        verify(logRepository).findByUsuarioId(usuarioId);
    }

    @Test
    void listarPorTabela_ComTabelaValida_DeveRetornarLista() {
        // Arrange
        String tabela = "usuarios";
        List<Log> logs = Arrays.asList(log);
        when(logRepository.findByTabela(tabela)).thenReturn(logs);

        // Act
        List<Log> resultado = logService.listarPorTabela(tabela);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(log, resultado.get(0));
        verify(logRepository).findByTabela(tabela);
    }

    @Test
    void listarPorAcao_ComAcaoValida_DeveRetornarLista() {
        // Arrange
        String acao = "INSERT";
        List<Log> logs = Arrays.asList(log);
        when(logRepository.findByAcao(acao)).thenReturn(logs);

        // Act
        List<Log> resultado = logService.listarPorAcao(acao);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(log, resultado.get(0));
        verify(logRepository).findByAcao(acao);
    }

    @Test
    void listarPorPeriodo_ComPeriodoValido_DeveRetornarLista() {
        // Arrange
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fim = LocalDateTime.now();
        List<Log> logs = Arrays.asList(log);
        when(logRepository.findByCriadoEmBetween(inicio, fim)).thenReturn(logs);

        // Act
        List<Log> resultado = logService.listarPorPeriodo(inicio, fim);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(log, resultado.get(0));
        verify(logRepository).findByCriadoEmBetween(inicio, fim);
    }

    @Test
    void listarAuditoriaPorTabela_ComTabelaValida_DeveRetornarLista() {
        // Arrange
        String tabela = "usuarios";
        List<Log> logs = Arrays.asList(log);
        when(logRepository.findByTabelaWithUsuario(tabela)).thenReturn(logs);

        // Act
        List<Log> resultado = logService.listarAuditoriaPorTabela(tabela);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(log, resultado.get(0));
        verify(logRepository).findByTabelaWithUsuario(tabela);
    }

    @Test
    void listarAuditoriaPorAcao_ComAcaoValida_DeveRetornarLista() {
        // Arrange
        String acao = "UPDATE";
        List<Log> logs = Arrays.asList(log);
        when(logRepository.findByAcaoWithUsuario(acao)).thenReturn(logs);

        // Act
        List<Log> resultado = logService.listarAuditoriaPorAcao(acao);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(log, resultado.get(0));
        verify(logRepository).findByAcaoWithUsuario(acao);
    }

    @Test
    void contarPorUsuario_ComUsuarioValido_DeveRetornarQuantidade() {
        // Arrange
        long quantidade = 5L;
        when(logRepository.countByUsuarioId(usuarioId)).thenReturn(quantidade);

        // Act
        long resultado = logService.contarPorUsuario(usuarioId);

        // Assert
        assertEquals(quantidade, resultado);
        verify(logRepository).countByUsuarioId(usuarioId);
    }

    @Test
    void contarPorTabela_ComTabelaValida_DeveRetornarQuantidade() {
        // Arrange
        String tabela = "usuarios";
        long quantidade = 10L;
        when(logRepository.countByTabela(tabela)).thenReturn(quantidade);

        // Act
        long resultado = logService.contarPorTabela(tabela);

        // Assert
        assertEquals(quantidade, resultado);
        verify(logRepository).countByTabela(tabela);
    }

    @Test
    void contarPorAcao_ComAcaoValida_DeveRetornarQuantidade() {
        // Arrange
        String acao = "INSERT";
        long quantidade = 15L;
        when(logRepository.countByAcao(acao)).thenReturn(quantidade);

        // Act
        long resultado = logService.contarPorAcao(acao);

        // Assert
        assertEquals(quantidade, resultado);
        verify(logRepository).countByAcao(acao);
    }

    @Test
    void contarPorPeriodo_ComPeriodoValido_DeveRetornarQuantidade() {
        // Arrange
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fim = LocalDateTime.now();
        long quantidade = 20L;
        when(logRepository.countByCriadoEmBetween(inicio, fim)).thenReturn(quantidade);

        // Act
        long resultado = logService.contarPorPeriodo(inicio, fim);

        // Assert
        assertEquals(quantidade, resultado);
        verify(logRepository).countByCriadoEmBetween(inicio, fim);
    }

    @Test
    void listarTodos_DeveRetornarLista() {
        // Arrange
        List<Log> logs = Arrays.asList(log);
        when(logRepository.findAll()).thenReturn(logs);

        // Act
        List<Log> resultado = logService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(log, resultado.get(0));
        verify(logRepository).findAll();
    }

    @Test
    void listarPorUsuarioComDados_ComUsuarioValido_DeveRetornarLista() {
        // Arrange
        List<Log> logs = Arrays.asList(log);
        when(logRepository.findByUsuarioIdWithUsuario(usuarioId)).thenReturn(logs);

        // Act
        List<Log> resultado = logService.listarPorUsuarioComDados(usuarioId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(log, resultado.get(0));
        verify(logRepository).findByUsuarioIdWithUsuario(usuarioId);
    }

    @Test
    void listarPorPeriodoComDados_ComPeriodoValido_DeveRetornarLista() {
        // Arrange
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fim = LocalDateTime.now();
        List<Log> logs = Arrays.asList(log);
        when(logRepository.findByCriadoEmBetweenWithUsuario(inicio, fim)).thenReturn(logs);

        // Act
        List<Log> resultado = logService.listarPorPeriodoComDados(inicio, fim);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(log, resultado.get(0));
        verify(logRepository).findByCriadoEmBetweenWithUsuario(inicio, fim);
    }
} 