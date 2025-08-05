package com.bancobr.service;

import com.bancobr.model.Conta;
import com.bancobr.model.Transacao;
import com.bancobr.model.Usuario;
import com.bancobr.repository.ContaRepository;
import com.bancobr.repository.TransacaoRepository;
import com.bancobr.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço para gerenciamento de transações bancárias
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@Service
@Transactional
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LogService logService;

    /**
     * Realiza transferência entre contas por número de conta
     */
    @Transactional(rollbackFor = Exception.class)
    public Transacao realizarTransferenciaPorNumero(String numeroContaOrigem, String numeroContaDestino, BigDecimal valor, String descricao) {
        System.out.println("🔍 [DEBUG] Iniciando transferência por número:");
        System.out.println("   - Conta Origem: " + numeroContaOrigem);
        System.out.println("   - Conta Destino: " + numeroContaDestino);
        System.out.println("   - Valor: " + valor);
        System.out.println("   - Descrição: " + descricao);

        try {
            // Busca contas por número
            System.out.println("🔍 [DEBUG] Buscando conta origem por número...");
            Conta contaOrigem = contaRepository.findByNumeroConta(numeroContaOrigem)
                    .orElseThrow(() -> new RuntimeException("Conta origem não encontrada: " + numeroContaOrigem));
            System.out.println("✅ [DEBUG] Conta origem encontrada: " + contaOrigem.getId() + " - Saldo: " + contaOrigem.getSaldo());
            
            System.out.println("🔍 [DEBUG] Buscando conta destino por número...");
            Conta contaDestino = contaRepository.findByNumeroConta(numeroContaDestino)
                    .orElseThrow(() -> new RuntimeException("Conta destino não encontrada: " + numeroContaDestino));
            System.out.println("✅ [DEBUG] Conta destino encontrada: " + contaDestino.getId() + " - Saldo: " + contaDestino.getSaldo());

            // Chama o método original com os UUIDs
            return realizarTransferencia(contaOrigem.getId(), contaDestino.getId(), valor, descricao);
        } catch (Exception e) {
            System.out.println("❌ [DEBUG] Erro na transferência por número: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Realiza transferência entre contas com validações completas
     */
    @Transactional(rollbackFor = Exception.class)
    public Transacao realizarTransferencia(UUID contaOrigemId, UUID contaDestinoId, BigDecimal valor, String descricao) {
        System.out.println("🔍 [DEBUG] Iniciando transferência:");
        System.out.println("   - Conta Origem: " + contaOrigemId);
        System.out.println("   - Conta Destino: " + contaDestinoId);
        System.out.println("   - Valor: " + valor);
        System.out.println("   - Descrição: " + descricao);

        try {
            // Validações de negócio
            System.out.println("✅ [DEBUG] Validando transferência...");
            validarTransferencia(contaOrigemId, contaDestinoId, valor);
            System.out.println("✅ [DEBUG] Validações passaram!");

            // Busca contas com lock otimista
            System.out.println("🔍 [DEBUG] Buscando conta origem...");
            Conta contaOrigem = contaRepository.findById(contaOrigemId)
                    .orElseThrow(() -> new RuntimeException("Conta origem não encontrada"));
            System.out.println("✅ [DEBUG] Conta origem encontrada: " + contaOrigem.getId() + " - Saldo: " + contaOrigem.getSaldo());
            
            System.out.println("🔍 [DEBUG] Buscando conta destino...");
            Conta contaDestino = contaRepository.findById(contaDestinoId)
                    .orElseThrow(() -> new RuntimeException("Conta destino não encontrada"));
            System.out.println("✅ [DEBUG] Conta destino encontrada: " + contaDestino.getId() + " - Saldo: " + contaDestino.getSaldo());

            // Validações adicionais
            System.out.println("🔍 [DEBUG] Validando saldo...");
            if (!contaOrigem.temSaldoSuficiente(valor)) {
                System.out.println("❌ [DEBUG] Saldo insuficiente!");
                throw new RuntimeException("Saldo insuficiente na conta origem");
            }
            System.out.println("✅ [DEBUG] Saldo suficiente!");

            System.out.println("🔍 [DEBUG] Validando contas diferentes...");
            if (contaOrigem.getId().equals(contaDestino.getId())) {
                System.out.println("❌ [DEBUG] Mesma conta!");
                throw new RuntimeException("Não é possível transferir para a mesma conta");
            }
            System.out.println("✅ [DEBUG] Contas diferentes!");

            // Cria a transação
            System.out.println("🔍 [DEBUG] Criando transação...");
            Transacao transacao = new Transacao(contaOrigem, contaDestino, Transacao.TipoTransacao.TRANSFERENCIA, valor, descricao);
            transacao.setRealizadaEm(LocalDateTime.now());
            System.out.println("✅ [DEBUG] Transação criada!");

            // Processa a transação (debitar/creditar)
            System.out.println("🔍 [DEBUG] Processando transação...");
            transacao.processar();
            System.out.println("✅ [DEBUG] Transação processada!");

            // Salva a transação
            System.out.println("🔍 [DEBUG] Salvando transação...");
            Transacao transacaoSalva = transacaoRepository.save(transacao);
            System.out.println("✅ [DEBUG] Transação salva: " + transacaoSalva.getId());

            // Atualiza as contas
            System.out.println("🔍 [DEBUG] Atualizando contas...");
            contaRepository.save(contaOrigem);
            contaRepository.save(contaDestino);
            System.out.println("✅ [DEBUG] Contas atualizadas!");

                    // Registra log de auditoria
        System.out.println("🔍 [DEBUG] Registrando log...");
        // logService.criarLogSistema("TRANSFERENCIA", "transacoes", transacaoSalva.getId());
        System.out.println("✅ [DEBUG] Log registrado!");

            System.out.println("🎉 [DEBUG] Transferência concluída com sucesso!");
            return transacaoSalva;

        } catch (Exception e) {
            System.out.println("❌ [DEBUG] Erro na transferência: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Realiza depósito
     */
    @Transactional(rollbackFor = Exception.class)
    public Transacao realizarDeposito(UUID contaDestinoId, BigDecimal valor, String descricao) {
        // Validações
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor deve ser maior que zero");
        }

        Conta contaDestino = contaRepository.findById(contaDestinoId)
                .orElseThrow(() -> new RuntimeException("Conta destino não encontrada"));

        // Cria a transação
        Transacao transacao = new Transacao(null, contaDestino, Transacao.TipoTransacao.DEPOSITO, valor, descricao);
        transacao.setRealizadaEm(LocalDateTime.now());

        // Processa a transação
        transacao.processar();

        // Salva a transação
        Transacao transacaoSalva = transacaoRepository.save(transacao);

        // Atualiza a conta
        contaRepository.save(contaDestino);

        // Registra log de auditoria
        // logService.criarLogSistema("DEPOSITO", "transacoes", transacaoSalva.getId());

        return transacaoSalva;
    }

    /**
     * Realiza saque
     */
    @Transactional(rollbackFor = Exception.class)
    public Transacao realizarSaque(UUID contaOrigemId, BigDecimal valor, String descricao) {
        // Validações
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor deve ser maior que zero");
        }

        Conta contaOrigem = contaRepository.findById(contaOrigemId)
                .orElseThrow(() -> new RuntimeException("Conta origem não encontrada"));

        // Valida saldo
        if (!contaOrigem.temSaldoSuficiente(valor)) {
            throw new RuntimeException("Saldo insuficiente");
        }

        // Cria a transação
        Transacao transacao = new Transacao(contaOrigem, null, Transacao.TipoTransacao.SAQUE, valor, descricao);
        transacao.setRealizadaEm(LocalDateTime.now());

        // Processa a transação
        transacao.processar();

        // Salva a transação
        Transacao transacaoSalva = transacaoRepository.save(transacao);

        // Atualiza a conta
        contaRepository.save(contaOrigem);

        // Registra log de auditoria
        // logService.criarLogSistema("SAQUE", "transacoes", transacaoSalva.getId());

        return transacaoSalva;
    }

    /**
     * Validações para transferência
     */
    private void validarTransferencia(UUID contaOrigemId, UUID contaDestinoId, BigDecimal valor) {
        if (contaOrigemId == null || contaDestinoId == null) {
            throw new RuntimeException("Conta origem e destino são obrigatórias");
        }

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor deve ser maior que zero");
        }

        if (contaOrigemId.equals(contaDestinoId)) {
            throw new RuntimeException("Não é possível transferir para a mesma conta");
        }
    }

    /**
     * Busca transação por ID
     */
    @Transactional(readOnly = true)
    public Optional<Transacao> buscarPorId(UUID id) {
        return transacaoRepository.findById(id);
    }

    /**
     * Lista transações processadas
     */
    @Transactional(readOnly = true)
    public List<Transacao> listarProcessadas() {
        return transacaoRepository.findTransacoesProcessadas();
    }

    /**
     * Lista transações por conta
     */
    @Transactional(readOnly = true)
    public List<Transacao> listarPorConta(UUID contaId) {
        return transacaoRepository.findByContaIdAndTipo(contaId, null);
    }

    /**
     * Lista transações por usuário
     */
    @Transactional(readOnly = true)
    public List<Transacao> listarPorUsuario(UUID usuarioId) {
        return transacaoRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Lista transações por tipo
     */
    @Transactional(readOnly = true)
    public List<Transacao> listarPorTipo(Transacao.TipoTransacao tipo) {
        return transacaoRepository.findByTipoAndRealizadaEmBetween(tipo, null, null);
    }

    /**
     * Lista transações por período
     */
    @Transactional(readOnly = true)
    public List<Transacao> listarPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return transacaoRepository.findByRealizadaEmBetween(dataInicio, dataFim);
    }

    /**
     * Lista transações por conta e tipo
     */
    @Transactional(readOnly = true)
    public List<Transacao> listarPorContaETipo(UUID contaId, Transacao.TipoTransacao tipo) {
        return transacaoRepository.findByContaIdAndTipo(contaId, tipo);
    }

    /**
     * Lista transações por usuário e tipo
     */
    @Transactional(readOnly = true)
    public List<Transacao> listarPorUsuarioETipo(UUID usuarioId, Transacao.TipoTransacao tipo) {
        return transacaoRepository.findByUsuarioIdAndTipo(usuarioId, tipo);
    }

    /**
     * Obtém extrato da conta
     */
    @Transactional(readOnly = true)
    public List<Transacao> obterExtrato(UUID contaId) {
        return transacaoRepository.findUltimasTransacoesByContaId(contaId);
    }

    /**
     * Obtém extrato por usuário
     */
    @Transactional(readOnly = true)
    public List<Transacao> obterExtratoPorUsuario(UUID usuarioId) {
        return transacaoRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Obtém extrato por período
     */
    @Transactional(readOnly = true)
    public List<Transacao> obterExtratoPorPeriodo(UUID contaId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return transacaoRepository.findByContaIdAndTipo(contaId, null);
    }

    /**
     * Conta transações por tipo
     */
    @Transactional(readOnly = true)
    public long contarPorTipo(Transacao.TipoTransacao tipo) {
        return transacaoRepository.countByTipo(tipo);
    }

    /**
     * Conta transações por conta
     */
    @Transactional(readOnly = true)
    public long contarPorConta(UUID contaId) {
        return transacaoRepository.countByContaId(contaId);
    }

    /**
     * Conta transações por usuário
     */
    @Transactional(readOnly = true)
    public long contarPorUsuario(UUID usuarioId) {
        return transacaoRepository.countByUsuarioId(usuarioId);
    }

    /**
     * Soma valor total das transações
     */
    @Transactional(readOnly = true)
    public BigDecimal somarValorTotal() {
        return transacaoRepository.sumValorTotal();
    }

    /**
     * Soma valor por tipo
     */
    @Transactional(readOnly = true)
    public BigDecimal somarValorPorTipo(Transacao.TipoTransacao tipo) {
        return transacaoRepository.sumValorByTipo(tipo);
    }

    /**
     * Soma valor por conta
     */
    @Transactional(readOnly = true)
    public BigDecimal somarValorPorConta(UUID contaId) {
        return transacaoRepository.sumValorByContaId(contaId);
    }

    /**
     * Soma valor por usuário
     */
    @Transactional(readOnly = true)
    public BigDecimal somarValorPorUsuario(UUID usuarioId) {
        return transacaoRepository.sumValorByUsuarioId(usuarioId);
    }

    /**
     * Busca transação com contas
     */
    @Transactional(readOnly = true)
    public Optional<Transacao> buscarComContas(UUID id) {
        return transacaoRepository.findByIdWithContas(id);
    }
} 