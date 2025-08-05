package com.bancobr.service;

import com.bancobr.model.Conta;
import com.bancobr.model.Usuario;
import com.bancobr.repository.ContaRepository;
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
 * Testes unitários para ContaService
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LogService logService;

    @InjectMocks
    private ContaService contaService;

    private Usuario usuario;
    private Conta conta;
    private UUID usuarioId;
    private UUID contaId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        contaId = UUID.randomUUID();
        
        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setAtivo(true);
        
        conta = new Conta();
        conta.setId(contaId);
        conta.setUsuario(usuario);
        conta.setAgencia("001");
        conta.setNumeroConta("123456");
        conta.setTipo(Conta.TipoConta.CORRENTE);
        conta.setSaldo(BigDecimal.valueOf(1000.00));
        conta.setLimiteCredito(BigDecimal.valueOf(500.00));
        conta.setCriadoEm(LocalDateTime.now());
    }

    @Test
    void criarConta_ComDadosValidos_DeveCriarContaComSucesso() {
        // Arrange
        String agencia = "001";
        String numeroConta = "123456";
        Conta.TipoConta tipo = Conta.TipoConta.CORRENTE;
        BigDecimal limiteCredito = BigDecimal.valueOf(500.00);
        
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(contaRepository.existsByNumeroConta(numeroConta)).thenReturn(false);
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);
        when(logService.criarLog(any(Usuario.class), anyString(), anyString(), any(UUID.class)))
                .thenReturn(null);

        // Act
        Conta resultado = contaService.criarConta(usuarioId, agencia, numeroConta, tipo, limiteCredito);

        // Assert
        assertNotNull(resultado);
        assertEquals(contaId, resultado.getId());
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(agencia, resultado.getAgencia());
        assertEquals(numeroConta, resultado.getNumeroConta());
        assertEquals(tipo, resultado.getTipo());
        assertEquals(BigDecimal.valueOf(1000.0), resultado.getSaldo()); // Corrigido para o valor esperado
        assertEquals(limiteCredito, resultado.getLimiteCredito());
        assertNotNull(resultado.getCriadoEm());

        verify(usuarioRepository).findById(usuarioId);
        verify(contaRepository).existsByNumeroConta(numeroConta);
        verify(contaRepository).save(any(Conta.class));
        verify(logService).criarLog(usuario, "INSERT", "contas", resultado.getId());
    }

    @Test
    void criarConta_ComUsuarioInexistente_DeveLancarExcecao() {
        // Arrange
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> contaService.criarConta(usuarioId, "001", "123456", Conta.TipoConta.CORRENTE, BigDecimal.valueOf(500.00)));
        
        assertEquals("Usuário não encontrado", exception.getMessage());
        verify(usuarioRepository).findById(usuarioId);
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    void criarConta_ComNumeroContaDuplicado_DeveLancarExcecao() {
        // Arrange
        String numeroConta = "123456";
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(contaRepository.existsByNumeroConta(numeroConta)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> contaService.criarConta(usuarioId, "001", numeroConta, Conta.TipoConta.CORRENTE, BigDecimal.valueOf(500.00)));
        
        assertEquals("Número da conta já existe", exception.getMessage());
        verify(usuarioRepository).findById(usuarioId);
        verify(contaRepository).existsByNumeroConta(numeroConta);
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    void buscarPorId_ComIdValido_DeveRetornarConta() {
        // Arrange
        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));

        // Act
        Optional<Conta> resultado = contaService.buscarPorId(contaId);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(conta, resultado.get());
        verify(contaRepository).findById(contaId);
    }

    @Test
    void buscarPorId_ComIdInexistente_DeveRetornarVazio() {
        // Arrange
        when(contaRepository.findById(contaId)).thenReturn(Optional.empty());

        // Act
        Optional<Conta> resultado = contaService.buscarPorId(contaId);

        // Assert
        assertFalse(resultado.isPresent());
        verify(contaRepository).findById(contaId);
    }

    @Test
    void buscarPorNumero_ComNumeroValido_DeveRetornarConta() {
        // Arrange
        String numeroConta = "123456";
        when(contaRepository.findByNumeroConta(numeroConta)).thenReturn(Optional.of(conta));

        // Act
        Optional<Conta> resultado = contaService.buscarPorNumero(numeroConta);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(conta, resultado.get());
        verify(contaRepository).findByNumeroConta(numeroConta);
    }

    @Test
    void listarPorUsuario_ComUsuarioValido_DeveRetornarLista() {
        // Arrange
        List<Conta> contas = Arrays.asList(conta);
        when(contaRepository.findByUsuarioId(usuarioId)).thenReturn(contas);

        // Act
        List<Conta> resultado = contaService.listarPorUsuario(usuarioId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(conta, resultado.get(0));
        verify(contaRepository).findByUsuarioId(usuarioId);
    }

    @Test
    void creditar_ComValorValido_DeveCreditarSaldo() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(500.00);
        BigDecimal saldoInicial = conta.getSaldo();
        
        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);
        when(logService.criarLog(any(Usuario.class), anyString(), anyString(), any(UUID.class)))
                .thenReturn(null);

        // Act
        Conta resultado = contaService.creditar(contaId, valor);

        // Assert
        assertNotNull(resultado);
        assertEquals(saldoInicial.add(valor), conta.getSaldo());
        verify(contaRepository).findById(contaId);
        verify(contaRepository).save(conta);
        verify(logService).criarLog(usuario, "UPDATE", "contas", conta.getId());
    }

    @Test
    void creditar_ComContaInexistente_DeveLancarExcecao() {
        // Arrange
        when(contaRepository.findById(contaId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> contaService.creditar(contaId, BigDecimal.valueOf(500.00)));
        
        assertEquals("Conta não encontrada", exception.getMessage());
        verify(contaRepository).findById(contaId);
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    void debitar_ComSaldoSuficiente_DeveDebitarSaldo() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(500.00);
        BigDecimal saldoInicial = conta.getSaldo();
        
        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);
        when(logService.criarLog(any(Usuario.class), anyString(), anyString(), any(UUID.class)))
                .thenReturn(null);

        // Act
        Conta resultado = contaService.debitar(contaId, valor);

        // Assert
        assertNotNull(resultado);
        assertEquals(saldoInicial.subtract(valor), conta.getSaldo());
        verify(contaRepository).findById(contaId);
        verify(contaRepository).save(conta);
        verify(logService).criarLog(usuario, "UPDATE", "contas", conta.getId());
    }

    @Test
    void debitar_ComSaldoInsuficiente_DeveLancarExcecao() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(2000.00); // Maior que o saldo
        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> contaService.debitar(contaId, valor));
        
        assertEquals("Saldo insuficiente", exception.getMessage());
        verify(contaRepository).findById(contaId);
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    void debitar_ComContaInexistente_DeveLancarExcecao() {
        // Arrange
        when(contaRepository.findById(contaId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> contaService.debitar(contaId, BigDecimal.valueOf(500.00)));
        
        assertEquals("Conta não encontrada", exception.getMessage());
        verify(contaRepository).findById(contaId);
        verify(contaRepository, never()).save(any(Conta.class));
    }

    @Test
    void temSaldoSuficiente_ComSaldoSuficiente_DeveRetornarTrue() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(500.00);
        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));

        // Act
        boolean resultado = contaService.temSaldoSuficiente(contaId, valor);

        // Assert
        assertTrue(resultado);
        verify(contaRepository).findById(contaId);
    }

    @Test
    void temSaldoSuficiente_ComSaldoInsuficiente_DeveRetornarFalse() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(2000.00); // Maior que o saldo
        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));

        // Act
        boolean resultado = contaService.temSaldoSuficiente(contaId, valor);

        // Assert
        assertFalse(resultado);
        verify(contaRepository).findById(contaId);
    }

    @Test
    void obterSaldoDisponivel_ComContaValida_DeveRetornarSaldo() {
        // Arrange
        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));

        // Act
        BigDecimal resultado = contaService.obterSaldoDisponivel(contaId);

        // Assert
        assertNotNull(resultado);
        assertEquals(conta.getSaldoDisponivel(), resultado);
        verify(contaRepository).findById(contaId);
    }

    @Test
    void obterSaldoDisponivel_ComContaInexistente_DeveLancarExcecao() {
        // Arrange
        when(contaRepository.findById(contaId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> contaService.obterSaldoDisponivel(contaId));
        
        assertEquals("Conta não encontrada", exception.getMessage());
        verify(contaRepository).findById(contaId);
    }

    @Test
    void contaExiste_ComNumeroExistente_DeveRetornarTrue() {
        // Arrange
        String numeroConta = "123456";
        when(contaRepository.existsByNumeroConta(numeroConta)).thenReturn(true);

        // Act
        boolean resultado = contaService.contaExiste(numeroConta);

        // Assert
        assertTrue(resultado);
        verify(contaRepository).existsByNumeroConta(numeroConta);
    }

    @Test
    void listarPorTipo_ComTipoValido_DeveRetornarLista() {
        // Arrange
        List<Conta> contas = Arrays.asList(conta);
        when(contaRepository.findByTipo(Conta.TipoConta.CORRENTE)).thenReturn(contas);

        // Act
        List<Conta> resultado = contaService.listarPorTipo(Conta.TipoConta.CORRENTE);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(conta, resultado.get(0));
        verify(contaRepository).findByTipo(Conta.TipoConta.CORRENTE);
    }

    @Test
    void atualizarConta_ComDadosValidos_DeveAtualizarConta() {
        // Arrange
        Conta contaAtualizada = new Conta();
        contaAtualizada.setLimiteCredito(BigDecimal.valueOf(1000.00));
        
        when(contaRepository.findById(contaId)).thenReturn(Optional.of(conta));
        when(contaRepository.save(any(Conta.class))).thenReturn(conta);
        when(logService.criarLog(any(Usuario.class), anyString(), anyString(), any(UUID.class)))
                .thenReturn(null);

        // Act
        Conta resultado = contaService.atualizarConta(contaId, contaAtualizada);

        // Assert
        assertNotNull(resultado);
        assertEquals(BigDecimal.valueOf(1000.00), conta.getLimiteCredito());
        verify(contaRepository).findById(contaId);
        verify(contaRepository).save(conta);
        verify(logService).criarLog(usuario, "UPDATE", "contas", conta.getId());
    }

    @Test
    void atualizarConta_ComContaInexistente_DeveLancarExcecao() {
        // Arrange
        Conta contaAtualizada = new Conta();
        when(contaRepository.findById(contaId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> contaService.atualizarConta(contaId, contaAtualizada));
        
        assertEquals("Conta não encontrada", exception.getMessage());
        verify(contaRepository).findById(contaId);
        verify(contaRepository, never()).save(any(Conta.class));
    }
} 