package com.bancobr.integration;

import com.bancobr.model.Conta;
import com.bancobr.model.Transacao;
import com.bancobr.model.Usuario;
import com.bancobr.repository.ContaRepository;
import com.bancobr.repository.TransacaoRepository;
import com.bancobr.repository.UsuarioRepository;
import com.bancobr.service.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração para TransacaoService
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransacaoIntegrationTest {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    private Usuario usuario1;
    private Usuario usuario2;
    private Conta contaOrigem;
    private Conta contaDestino;

    @BeforeEach
    void setUp() {
        // Limpa o banco antes de cada teste
        transacaoRepository.deleteAll();
        contaRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Cria usuários
        usuario1 = new Usuario();
        usuario1.setNome("João Silva");
        usuario1.setEmail("joao@email.com");
        usuario1.setCpf("12345678901");
        usuario1.setSenhaHash("senha123");
        usuario1.setTipo(Usuario.TipoUsuario.CLIENTE);
        usuario1.setAtivo(true);
        usuario1 = usuarioRepository.save(usuario1);

        usuario2 = new Usuario();
        usuario2.setNome("Maria Santos");
        usuario2.setEmail("maria@email.com");
        usuario2.setCpf("98765432100");
        usuario2.setSenhaHash("senha456");
        usuario2.setTipo(Usuario.TipoUsuario.CLIENTE);
        usuario2.setAtivo(true);
        usuario2 = usuarioRepository.save(usuario2);

        // Cria contas
        contaOrigem = new Conta();
        contaOrigem.setUsuario(usuario1);
        contaOrigem.setAgencia("001");
        contaOrigem.setNumeroConta("123456");
        contaOrigem.setTipo(Conta.TipoConta.CORRENTE);
        contaOrigem.setSaldo(BigDecimal.valueOf(2000.00));
        contaOrigem.setLimiteCredito(BigDecimal.valueOf(500.00));
        contaOrigem = contaRepository.save(contaOrigem);

        contaDestino = new Conta();
        contaDestino.setUsuario(usuario2);
        contaDestino.setAgencia("002");
        contaDestino.setNumeroConta("654321");
        contaDestino.setTipo(Conta.TipoConta.CORRENTE);
        contaDestino.setSaldo(BigDecimal.valueOf(1000.00));
        contaDestino.setLimiteCredito(BigDecimal.valueOf(300.00));
        contaDestino = contaRepository.save(contaDestino);
    }

    @Test
    void realizarTransferencia_ComDadosValidos_DeveRealizarTransferencia() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(500.00);
        String descricao = "Transferência teste";

        // Act
        Transacao transacao = transacaoService.realizarTransferencia(
                contaOrigem.getId(), 
                contaDestino.getId(), 
                valor, 
                descricao
        );

        // Assert
        assertNotNull(transacao);
        assertEquals(contaOrigem.getId(), transacao.getContaOrigem().getId());
        assertEquals(contaDestino.getId(), transacao.getContaDestino().getId());
        assertEquals(valor, transacao.getValor());
        assertEquals(descricao, transacao.getDescricao());
        assertEquals(Transacao.TipoTransacao.TRANSFERENCIA, transacao.getTipo());

        // Verifica se os saldos foram atualizados
        Conta contaOrigemAtualizada = contaRepository.findById(contaOrigem.getId()).orElse(null);
        Conta contaDestinoAtualizada = contaRepository.findById(contaDestino.getId()).orElse(null);

        assertNotNull(contaOrigemAtualizada);
        assertNotNull(contaDestinoAtualizada);
        assertEquals(BigDecimal.valueOf(1500.00), contaOrigemAtualizada.getSaldo()); // 2000 - 500
        assertEquals(BigDecimal.valueOf(1500.00), contaDestinoAtualizada.getSaldo()); // 1000 + 500
    }

    @Test
    void realizarTransferencia_ComSaldoInsuficiente_DeveLancarExcecao() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(3000.00); // Maior que o saldo

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transacaoService.realizarTransferencia(
                    contaOrigem.getId(), 
                    contaDestino.getId(), 
                    valor, 
                    "Transferência com saldo insuficiente"
            );
        });

        // Verifica se os saldos não foram alterados
        Conta contaOrigemAtualizada = contaRepository.findById(contaOrigem.getId()).orElse(null);
        Conta contaDestinoAtualizada = contaRepository.findById(contaDestino.getId()).orElse(null);

        assertNotNull(contaOrigemAtualizada);
        assertNotNull(contaDestinoAtualizada);
        assertEquals(BigDecimal.valueOf(2000.00), contaOrigemAtualizada.getSaldo());
        assertEquals(BigDecimal.valueOf(1000.00), contaDestinoAtualizada.getSaldo());
    }

    @Test
    void realizarDeposito_ComDadosValidos_DeveRealizarDeposito() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(500.00);
        String descricao = "Depósito teste";

        // Act
        Transacao transacao = transacaoService.realizarDeposito(
                contaDestino.getId(), 
                valor, 
                descricao
        );

        // Assert
        assertNotNull(transacao);
        assertNull(transacao.getContaOrigem());
        assertEquals(contaDestino.getId(), transacao.getContaDestino().getId());
        assertEquals(valor, transacao.getValor());
        assertEquals(descricao, transacao.getDescricao());
        assertEquals(Transacao.TipoTransacao.DEPOSITO, transacao.getTipo());

        // Verifica se o saldo foi atualizado
        Conta contaDestinoAtualizada = contaRepository.findById(contaDestino.getId()).orElse(null);
        assertNotNull(contaDestinoAtualizada);
        assertEquals(BigDecimal.valueOf(1500.00), contaDestinoAtualizada.getSaldo()); // 1000 + 500
    }

    @Test
    void realizarSaque_ComSaldoSuficiente_DeveRealizarSaque() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(500.00);
        String descricao = "Saque teste";

        // Act
        Transacao transacao = transacaoService.realizarSaque(
                contaOrigem.getId(), 
                valor, 
                descricao
        );

        // Assert
        assertNotNull(transacao);
        assertEquals(contaOrigem.getId(), transacao.getContaOrigem().getId());
        assertNull(transacao.getContaDestino());
        assertEquals(valor, transacao.getValor());
        assertEquals(descricao, transacao.getDescricao());
        assertEquals(Transacao.TipoTransacao.SAQUE, transacao.getTipo());

        // Verifica se o saldo foi atualizado
        Conta contaOrigemAtualizada = contaRepository.findById(contaOrigem.getId()).orElse(null);
        assertNotNull(contaOrigemAtualizada);
        assertEquals(BigDecimal.valueOf(1500.00), contaOrigemAtualizada.getSaldo()); // 2000 - 500
    }

    @Test
    void realizarSaque_ComSaldoInsuficiente_DeveLancarExcecao() {
        // Arrange
        BigDecimal valor = BigDecimal.valueOf(3000.00); // Maior que o saldo

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transacaoService.realizarSaque(
                    contaOrigem.getId(), 
                    valor, 
                    "Saque com saldo insuficiente"
            );
        });

        // Verifica se o saldo não foi alterado
        Conta contaOrigemAtualizada = contaRepository.findById(contaOrigem.getId()).orElse(null);
        assertNotNull(contaOrigemAtualizada);
        assertEquals(BigDecimal.valueOf(2000.00), contaOrigemAtualizada.getSaldo());
    }

    @Test
    void buscarPorId_ComIdValido_DeveRetornarTransacao() {
        // Arrange
        Transacao transacaoSalva = transacaoService.realizarTransferencia(
                contaOrigem.getId(), 
                contaDestino.getId(), 
                BigDecimal.valueOf(100.00), 
                "Transferência para teste"
        );

        // Act
        Optional<Transacao> resultado = transacaoService.buscarPorId(transacaoSalva.getId());

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(transacaoSalva.getId(), resultado.get().getId());
        assertEquals(transacaoSalva.getValor(), resultado.get().getValor());
    }

    @Test
    void listarPorConta_ComContaValida_DeveRetornarLista() {
        // Arrange
        transacaoService.realizarTransferencia(
                contaOrigem.getId(), 
                contaDestino.getId(), 
                BigDecimal.valueOf(100.00), 
                "Transferência 1"
        );
        transacaoService.realizarTransferencia(
                contaOrigem.getId(), 
                contaDestino.getId(), 
                BigDecimal.valueOf(200.00), 
                "Transferência 2"
        );

        // Act
        List<Transacao> transacoes = transacaoService.listarPorConta(contaOrigem.getId());

        // Assert
        assertEquals(2, transacoes.size());
        assertTrue(transacoes.stream().allMatch(t -> t.getContaOrigem().getId().equals(contaOrigem.getId())));
    }

    @Test
    void obterExtrato_ComContaValida_DeveRetornarLista() {
        // Arrange
        transacaoService.realizarTransferencia(
                contaOrigem.getId(), 
                contaDestino.getId(), 
                BigDecimal.valueOf(100.00), 
                "Transferência 1"
        );
        transacaoService.realizarDeposito(
                contaOrigem.getId(), 
                BigDecimal.valueOf(200.00), 
                "Depósito"
        );

        // Act
        List<Transacao> extrato = transacaoService.obterExtrato(contaOrigem.getId());

        // Assert
        assertNotNull(extrato);
        assertTrue(extrato.size() >= 2);
    }

    @Test
    void contarPorTipo_ComTipoValido_DeveRetornarQuantidade() {
        // Arrange
        transacaoService.realizarTransferencia(
                contaOrigem.getId(), 
                contaDestino.getId(), 
                BigDecimal.valueOf(100.00), 
                "Transferência"
        );
        transacaoService.realizarDeposito(
                contaDestino.getId(), 
                BigDecimal.valueOf(200.00), 
                "Depósito"
        );

        // Act
        long quantidadeTransferencias = transacaoService.contarPorTipo(Transacao.TipoTransacao.TRANSFERENCIA);
        long quantidadeDepositos = transacaoService.contarPorTipo(Transacao.TipoTransacao.DEPOSITO);

        // Assert
        assertEquals(1, quantidadeTransferencias);
        assertEquals(1, quantidadeDepositos);
    }
} 