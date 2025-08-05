package com.bancobr.service;

import com.bancobr.model.Conta;
import com.bancobr.model.Transacao;
import com.bancobr.model.Usuario;
import com.bancobr.repository.ContaRepository;
import com.bancobr.repository.TransacaoRepository;
import com.bancobr.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para TransacaoService
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LogService logService;

    @InjectMocks
    private TransacaoService transacaoService;

    private Usuario usuario1;
    private Usuario usuario2;
    private Conta contaOrigem;
    private Conta contaDestino;
    private Transacao transacao;
    private UUID usuario1Id;
    private UUID usuario2Id;
    private UUID contaOrigemId;
    private UUID contaDestinoId;
    private UUID transacaoId;

    @BeforeEach
    void setUp() {
        usuario1Id = UUID.randomUUID();
        usuario2Id = UUID.randomUUID();
        contaOrigemId = UUID.randomUUID();
        contaDestinoId = UUID.randomUUID();
        transacaoId = UUID.randomUUID();

        usuario1 = new Usuario();
        usuario1.setId(usuario1Id);
        usuario1.setNome("João Silva");
        usuario1.setEmail("joao@email.com");
        usuario1.setAtivo(true);

        usuario2 = new Usuario();
        usuario2.setId(usuario2Id);
        usuario2.setNome("Maria Santos");
        usuario2.setEmail("maria@email.com");
        usuario2.setAtivo(true);

        contaOrigem = new Conta();
        contaOrigem.setId(contaOrigemId);
        contaOrigem.setUsuario(usuario1);
        contaOrigem.setAgencia("001");
        contaOrigem.setNumeroConta("123456");
        contaOrigem.setTipo(Conta.TipoConta.CORRENTE);
        contaOrigem.setSaldo(BigDecimal.valueOf(2000.00));
        contaOrigem.setLimiteCredito(BigDecimal.valueOf(500.00));

        contaDestino = new Conta();
        contaDestino.setId(contaDestinoId);
        contaDestino.setUsuario(usuario2);
        contaDestino.setAgencia("002");
        contaDestino.setNumeroConta("654321");
        contaDestino.setTipo(Conta.TipoConta.CORRENTE);
        contaDestino.setSaldo(BigDecimal.valueOf(1000.00));
        contaDestino.setLimiteCredito(BigDecimal.valueOf(300.00));

        transacao = new Transacao();
        transacao.setId(transacaoId);
        transacao.setContaOrigem(contaOrigem);
        transacao.setContaDestino(contaDestino);
        transacao.setTipo(Transacao.TipoTransacao.TRANSFERENCIA);
        transacao.setValor(BigDecimal.valueOf(500.00));
        transacao.setDescricao("Transferência teste");
        transacao.setRealizadaEm(LocalDateTime.now());
    }

    @Test
    void realizarTransferencia_ComDadosValidos_DeveRealizarTransferencia() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(500.00);
        String descricao = "Transferência teste";
        
        when(contaRepository.findById(contaOrigemId)).thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findById(contaDestinoId)).thenReturn(Optional.of(contaDestino));
        when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacao);
        when(contaRepository.save(any(Conta.class))).thenReturn(contaOrigem).thenReturn(contaDestino);
        when(logService.criarLogSistema(anyString(), anyString(), any(UUID.class))).thenReturn(null);

        // Act
        Transacao resultado = transacaoService.realizarTransferencia(contaOrigemId, contaDestinoId, valor, descricao);

        // Assert
        assertNotNull(resultado);
        assertEquals(transacaoId, resultado.getId());
        assertEquals(contaOrigem, resultado.getContaOrigem());
        assertEquals(contaDestino, resultado.getContaDestino());
        assertEquals(valor, resultado.getValor());
        assertEquals(descricao, resultado.getDescricao());
        assertNotNull(resultado.getRealizadaEm());

        // Verifica se os saldos foram atualizados
        assertEquals(BigDecimal.valueOf(1500.00), contaOrigem.getSaldo()); // 2000 - 500
        assertEquals(BigDecimal.valueOf(1500.00), contaDestino.getSaldo()); // 1000 + 500

        verify(contaRepository).findById(contaOrigemId);
        verify(contaRepository).findById(contaDestinoId);
        verify(transacaoRepository).save(any(Transacao.class));
        verify(contaRepository, times(2)).save(any(Conta.class));
        verify(logService).criarLogSistema("TRANSFERENCIA", "transacoes", transacaoId);
    }

    @Test
    void realizarTransferencia_ComContaOrigemInexistente_DeveLancarExcecao() {
        // Arrange
        when(contaRepository.findById(contaOrigemId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> transacaoService.realizarTransferencia(contaOrigemId, contaDestinoId, BigDecimal.valueOf(500.00), "Teste"));
        
        assertEquals("Conta origem não encontrada", exception.getMessage());
        verify(contaRepository).findById(contaOrigemId);
        verify(contaRepository, never()).findById(contaDestinoId);
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void realizarTransferencia_ComContaDestinoInexistente_DeveLancarExcecao() {
        // Arrange
        when(contaRepository.findById(contaOrigemId)).thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findById(contaDestinoId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> transacaoService.realizarTransferencia(contaOrigemId, contaDestinoId, BigDecimal.valueOf(500.00), "Teste"));
        
        assertEquals("Conta destino não encontrada", exception.getMessage());
        verify(contaRepository).findById(contaOrigemId);
        verify(contaRepository).findById(contaDestinoId);
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void realizarTransferencia_ComSaldoInsuficiente_DeveLancarExcecao() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(3000.00); // Maior que o saldo
        when(contaRepository.findById(contaOrigemId)).thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findById(contaDestinoId)).thenReturn(Optional.of(contaDestino));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> transacaoService.realizarTransferencia(contaOrigemId, contaDestinoId, valor, "Teste"));
        
        assertEquals("Saldo insuficiente na conta origem", exception.getMessage());
        verify(contaRepository).findById(contaOrigemId);
        verify(contaRepository).findById(contaDestinoId);
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void realizarTransferencia_ComMesmaConta_DeveLancarExcecao() {
        // Arrange
        when(contaRepository.findById(contaOrigemId)).thenReturn(Optional.of(contaOrigem));
        when(contaRepository.findById(contaDestinoId)).thenReturn(Optional.of(contaOrigem)); // Mesma conta

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> transacaoService.realizarTransferencia(contaOrigemId, contaDestinoId, BigDecimal.valueOf(500.00), "Teste"));
        
        assertEquals("Não é possível transferir para a mesma conta", exception.getMessage());
        verify(contaRepository).findById(contaOrigemId);
        verify(contaRepository).findById(contaDestinoId);
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void realizarDeposito_ComDadosValidos_DeveRealizarDeposito() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(500.00);
        String descricao = "Depósito teste";
        
        Transacao transacaoDeposito = new Transacao();
        transacaoDeposito.setId(transacaoId);
        transacaoDeposito.setContaOrigem(null); // Depósito não tem conta origem
        transacaoDeposito.setContaDestino(contaDestino);
        transacaoDeposito.setValor(valor);
        transacaoDeposito.setDescricao(descricao);
        transacaoDeposito.setTipo(Transacao.TipoTransacao.DEPOSITO);
        transacaoDeposito.setRealizadaEm(LocalDateTime.now());
        
        when(contaRepository.findById(contaDestinoId)).thenReturn(Optional.of(contaDestino));
        when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacaoDeposito);
        when(contaRepository.save(any(Conta.class))).thenReturn(contaDestino);
        when(logService.criarLogSistema(anyString(), anyString(), any(UUID.class))).thenReturn(null);

        // Act
        Transacao resultado = transacaoService.realizarDeposito(contaDestinoId, valor, descricao);

        // Assert
        assertNotNull(resultado);
        assertEquals(transacaoId, resultado.getId());
        assertNull(resultado.getContaOrigem()); // Depósito não tem conta origem
        assertEquals(contaDestino, resultado.getContaDestino());
        assertEquals(valor, resultado.getValor());
        assertEquals(descricao, resultado.getDescricao());
        assertEquals(Transacao.TipoTransacao.DEPOSITO, resultado.getTipo());
        assertNotNull(resultado.getRealizadaEm());

        // Verifica se o saldo foi atualizado
        assertEquals(BigDecimal.valueOf(1500.00), contaDestino.getSaldo()); // 1000 + 500

        verify(contaRepository).findById(contaDestinoId);
        verify(transacaoRepository).save(any(Transacao.class));
        verify(contaRepository).save(contaDestino);
        verify(logService).criarLogSistema("DEPOSITO", "transacoes", transacaoId);
    }

    @Test
    void realizarDeposito_ComValorInvalido_DeveLancarExcecao() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(-100.00); // Valor negativo

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> transacaoService.realizarDeposito(contaDestinoId, valor, "Teste"));
        
        assertEquals("Valor deve ser maior que zero", exception.getMessage());
        verify(contaRepository, never()).findById(any(UUID.class));
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void realizarSaque_ComSaldoSuficiente_DeveRealizarSaque() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(500.00);
        String descricao = "Saque teste";
        
        Transacao transacaoSaque = new Transacao();
        transacaoSaque.setId(transacaoId);
        transacaoSaque.setContaOrigem(contaOrigem);
        transacaoSaque.setContaDestino(null); // Saque não tem conta destino
        transacaoSaque.setValor(valor);
        transacaoSaque.setDescricao(descricao);
        transacaoSaque.setTipo(Transacao.TipoTransacao.SAQUE);
        transacaoSaque.setRealizadaEm(LocalDateTime.now());
        
        when(contaRepository.findById(contaOrigemId)).thenReturn(Optional.of(contaOrigem));
        when(transacaoRepository.save(any(Transacao.class))).thenReturn(transacaoSaque);
        when(contaRepository.save(any(Conta.class))).thenReturn(contaOrigem);
        when(logService.criarLogSistema(anyString(), anyString(), any(UUID.class))).thenReturn(null);

        // Act
        Transacao resultado = transacaoService.realizarSaque(contaOrigemId, valor, descricao);

        // Assert
        assertNotNull(resultado);
        assertEquals(transacaoId, resultado.getId());
        assertEquals(contaOrigem, resultado.getContaOrigem());
        assertNull(resultado.getContaDestino()); // Saque não tem conta destino
        assertEquals(valor, resultado.getValor());
        assertEquals(descricao, resultado.getDescricao());
        assertEquals(Transacao.TipoTransacao.SAQUE, resultado.getTipo());
        assertNotNull(resultado.getRealizadaEm());

        // Verifica se o saldo foi atualizado
        assertEquals(BigDecimal.valueOf(1500.00), contaOrigem.getSaldo()); // 2000 - 500

        verify(contaRepository).findById(contaOrigemId);
        verify(transacaoRepository).save(any(Transacao.class));
        verify(contaRepository).save(contaOrigem);
        verify(logService).criarLogSistema("SAQUE", "transacoes", transacaoId);
    }

    @Test
    void realizarSaque_ComSaldoInsuficiente_DeveLancarExcecao() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(3000.00); // Maior que o saldo
        when(contaRepository.findById(contaOrigemId)).thenReturn(Optional.of(contaOrigem));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> transacaoService.realizarSaque(contaOrigemId, valor, "Teste"));
        
        assertEquals("Saldo insuficiente", exception.getMessage());
        verify(contaRepository).findById(contaOrigemId);
        verify(transacaoRepository, never()).save(any(Transacao.class));
    }

    @Test
    void buscarPorId_ComIdValido_DeveRetornarTransacao() {
        // Arrange
        when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.of(transacao));

        // Act
        Optional<Transacao> resultado = transacaoService.buscarPorId(transacaoId);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(transacao, resultado.get());
        verify(transacaoRepository).findById(transacaoId);
    }

    @Test
    void buscarPorId_ComIdInexistente_DeveRetornarVazio() {
        // Arrange
        when(transacaoRepository.findById(transacaoId)).thenReturn(Optional.empty());

        // Act
        Optional<Transacao> resultado = transacaoService.buscarPorId(transacaoId);

        // Assert
        assertFalse(resultado.isPresent());
        verify(transacaoRepository).findById(transacaoId);
    }

    @Test
    void listarProcessadas_DeveRetornarLista() {
        // Arrange
        List<Transacao> transacoes = Arrays.asList(transacao);
        when(transacaoRepository.findTransacoesProcessadas()).thenReturn(transacoes);

        // Act
        List<Transacao> resultado = transacaoService.listarProcessadas();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(transacao, resultado.get(0));
        verify(transacaoRepository).findTransacoesProcessadas();
    }

    @Test
    void listarPorConta_ComContaValida_DeveRetornarLista() {
        // Arrange
        List<Transacao> transacoes = Arrays.asList(transacao);
        when(transacaoRepository.findByContaIdAndTipo(contaOrigemId, null)).thenReturn(transacoes);

        // Act
        List<Transacao> resultado = transacaoService.listarPorConta(contaOrigemId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(transacao, resultado.get(0));
        verify(transacaoRepository).findByContaIdAndTipo(contaOrigemId, null);
    }

    @Test
    void listarPorTipo_ComTipoValido_DeveRetornarLista() {
        // Arrange
        List<Transacao> transacoes = Arrays.asList(transacao);
        when(transacaoRepository.findByTipoAndRealizadaEmBetween(Transacao.TipoTransacao.TRANSFERENCIA, null, null))
                .thenReturn(transacoes);

        // Act
        List<Transacao> resultado = transacaoService.listarPorTipo(Transacao.TipoTransacao.TRANSFERENCIA);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(transacao, resultado.get(0));
        verify(transacaoRepository).findByTipoAndRealizadaEmBetween(Transacao.TipoTransacao.TRANSFERENCIA, null, null);
    }

    @Test
    void obterExtrato_ComContaValida_DeveRetornarLista() {
        // Arrange
        List<Transacao> transacoes = Arrays.asList(transacao);
        when(transacaoRepository.findUltimasTransacoesByContaId(contaOrigemId)).thenReturn(transacoes);

        // Act
        List<Transacao> resultado = transacaoService.obterExtrato(contaOrigemId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(transacao, resultado.get(0));
        verify(transacaoRepository).findUltimasTransacoesByContaId(contaOrigemId);
    }

    @Test
    void contarPorTipo_ComTipoValido_DeveRetornarQuantidade() {
        // Arrange
        long quantidade = 5L;
        when(transacaoRepository.countByTipo(Transacao.TipoTransacao.TRANSFERENCIA)).thenReturn(quantidade);

        // Act
        long resultado = transacaoService.contarPorTipo(Transacao.TipoTransacao.TRANSFERENCIA);

        // Assert
        assertEquals(quantidade, resultado);
        verify(transacaoRepository).countByTipo(Transacao.TipoTransacao.TRANSFERENCIA);
    }

    @Test
    void somarValorTotal_DeveRetornarSoma() {
        // Arrange
        BigDecimal somaTotal = BigDecimal.valueOf(10000.00);
        when(transacaoRepository.sumValorTotal()).thenReturn(somaTotal);

        // Act
        BigDecimal resultado = transacaoService.somarValorTotal();

        // Assert
        assertEquals(somaTotal, resultado);
        verify(transacaoRepository).sumValorTotal();
    }

    @Test
    void buscarComContas_ComIdValido_DeveRetornarTransacao() {
        // Arrange
        when(transacaoRepository.findByIdWithContas(transacaoId)).thenReturn(Optional.of(transacao));

        // Act
        Optional<Transacao> resultado = transacaoService.buscarComContas(transacaoId);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(transacao, resultado.get());
        verify(transacaoRepository).findByIdWithContas(transacaoId);
    }
} 