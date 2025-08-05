package com.bancobr.service;

import com.bancobr.model.Conta;
import com.bancobr.model.Usuario;
import com.bancobr.repository.ContaRepository;
import com.bancobr.repository.UsuarioRepository;
import com.bancobr.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço para gerenciamento de contas bancárias
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@Service
@Transactional
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LogService logService;

    /**
     * Cria uma nova conta
     */
    public Conta criarConta(UUID usuarioId, String agencia, String numeroConta, Conta.TipoConta tipo, BigDecimal limiteCredito) {
        // Valida usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Valida se número da conta já existe
        if (contaRepository.existsByNumeroConta(numeroConta)) {
            throw new RuntimeException("Número da conta já existe");
        }

        // Cria a conta
        Conta conta = new Conta(usuario, agencia, numeroConta, tipo);
        conta.setLimiteCredito(limiteCredito != null ? limiteCredito : BigDecimal.ZERO);
        conta.setSaldo(BigDecimal.ZERO);
        conta.setCriadoEm(LocalDateTime.now());

        // Salva a conta
        Conta contaSalva = contaRepository.save(conta);

        // Registra log (comentado temporariamente para resolver problema jsonb)
        // logService.criarLog(usuario, "INSERT", "contas", contaSalva.getId());

        return contaSalva;
    }

    /**
     * Busca conta por ID
     */
    public Optional<Conta> buscarPorId(UUID id) {
        return contaRepository.findById(id);
    }

    /**
     * Busca conta por número
     */
    public Optional<Conta> buscarPorNumero(String numeroConta) {
        return contaRepository.findByNumeroConta(numeroConta);
    }

    /**
     * Lista contas por usuário
     */
    public List<Conta> listarPorUsuario(UUID usuarioId) {
        return contaRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Lista contas por agência
     */
    public List<Conta> listarPorAgencia(String agencia) {
        return contaRepository.findByAgencia(agencia);
    }

    /**
     * Lista contas por tipo
     */
    public List<Conta> listarPorTipo(Conta.TipoConta tipo) {
        return contaRepository.findByTipo(tipo);
    }

    /**
     * Lista contas por usuário e tipo
     */
    public List<Conta> listarPorUsuarioETipo(UUID usuarioId, Conta.TipoConta tipo) {
        return contaRepository.findByUsuarioIdAndTipo(usuarioId, tipo);
    }

    /**
     * Lista todas as contas
     */
    public List<Conta> listarTodas() {
        return contaRepository.findAll();
    }

    /**
     * Atualiza dados da conta
     */
    public Conta atualizarConta(UUID id, Conta contaAtualizada) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        // Atualiza campos permitidos
        if (contaAtualizada.getLimiteCredito() != null) {
            conta.setLimiteCredito(contaAtualizada.getLimiteCredito());
        }

        conta.setAtualizadoEm(LocalDateTime.now());
        Conta contaSalva = contaRepository.save(conta);

        // Registra log (comentado temporariamente para resolver problema jsonb)
        // logService.criarLog(conta.getUsuario(), "UPDATE", "contas", contaSalva.getId());

        return contaSalva;
    }

    /**
     * Atualiza saldo da conta
     */
    public Conta atualizarSaldo(UUID id, BigDecimal novoSaldo) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        conta.setSaldo(novoSaldo);
        conta.setAtualizadoEm(LocalDateTime.now());
        Conta contaSalva = contaRepository.save(conta);

        // Registra log (comentado temporariamente para resolver problema jsonb)
        // logService.criarLog(conta.getUsuario(), "UPDATE", "contas", contaSalva.getId());

        return contaSalva;
    }

    /**
     * Atualiza limite de crédito
     */
    public Conta atualizarLimiteCredito(UUID id, BigDecimal novoLimite) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        conta.setLimiteCredito(novoLimite);
        conta.setAtualizadoEm(LocalDateTime.now());
        Conta contaSalva = contaRepository.save(conta);

        // Registra log (comentado temporariamente para resolver problema jsonb)
        // logService.criarLog(conta.getUsuario(), "UPDATE", "contas", contaSalva.getId());

        return contaSalva;
    }

    /**
     * Credita valor na conta
     */
    public Conta creditar(UUID id, BigDecimal valor) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        conta.creditar(valor);
        conta.setAtualizadoEm(LocalDateTime.now());
        Conta contaSalva = contaRepository.save(conta);

        // Registra log (comentado temporariamente para resolver problema jsonb)
        // logService.criarLog(conta.getUsuario(), "UPDATE", "contas", contaSalva.getId());

        return contaSalva;
    }

    /**
     * Debita valor da conta
     */
    public Conta debitar(UUID id, BigDecimal valor) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if (!conta.temSaldoSuficiente(valor)) {
            throw new RuntimeException("Saldo insuficiente");
        }

        conta.debitar(valor);
        conta.setAtualizadoEm(LocalDateTime.now());
        Conta contaSalva = contaRepository.save(conta);

        // Registra log (comentado temporariamente para resolver problema jsonb)
        // logService.criarLog(conta.getUsuario(), "UPDATE", "contas", contaSalva.getId());

        return contaSalva;
    }

    /**
     * Verifica se conta existe
     */
    public boolean contaExiste(String numeroConta) {
        return contaRepository.existsByNumeroConta(numeroConta);
    }

    /**
     * Conta contas por usuário
     */
    public long contarPorUsuario(UUID usuarioId) {
        return contaRepository.countByUsuarioId(usuarioId);
    }

    /**
     * Conta contas por tipo
     */
    public long contarPorTipo(Conta.TipoConta tipo) {
        return contaRepository.countByTipo(tipo);
    }

    /**
     * Soma saldo total
     */
    public BigDecimal somarSaldoTotal() {
        return contaRepository.sumSaldoTotal();
    }

    /**
     * Soma saldo por usuário
     */
    public BigDecimal somarSaldoPorUsuario(UUID usuarioId) {
        return contaRepository.sumSaldoByUsuarioId(usuarioId);
    }

    /**
     * Soma saldo por tipo
     */
    public BigDecimal somarSaldoPorTipo(Conta.TipoConta tipo) {
        return contaRepository.sumSaldoByTipo(tipo);
    }

    /**
     * Busca conta com usuário
     */
    public Optional<Conta> buscarComUsuario(UUID id) {
        return contaRepository.findByIdWithUsuario(id);
    }

    /**
     * Busca conta com transações
     */
    public Optional<Conta> buscarComTransacoes(UUID id) {
        return contaRepository.findByIdWithTransacoes(id);
    }

    /**
     * Busca conta com usuário e transações
     */
    public Optional<Conta> buscarComUsuarioETransacoes(UUID id) {
        return contaRepository.findByIdWithUsuarioAndTransacoes(id);
    }

    /**
     * Lista contas por usuário com transações
     */
    public List<Conta> listarPorUsuarioComTransacoes(UUID usuarioId) {
        return contaRepository.findByUsuarioIdWithTransacoes(usuarioId);
    }

    /**
     * Lista contas com saldo maior que
     */
    public List<Conta> listarComSaldoMaiorQue(BigDecimal valor) {
        return contaRepository.findBySaldoGreaterThan(valor);
    }

    /**
     * Lista contas com saldo menor que
     */
    public List<Conta> listarComSaldoMenorQue(BigDecimal valor) {
        return contaRepository.findBySaldoLessThan(valor);
    }

    /**
     * Lista contas com saldo disponível maior que
     */
    public List<Conta> listarComSaldoDisponivelMaiorQue(BigDecimal valor) {
        return contaRepository.findBySaldoDisponivelGreaterThan(valor);
    }

    /**
     * Valida se conta tem saldo suficiente
     */
    public boolean temSaldoSuficiente(UUID id, BigDecimal valor) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        return conta.temSaldoSuficiente(valor);
    }

    /**
     * Obtém saldo disponível da conta
     */
    public BigDecimal obterSaldoDisponivel(UUID id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        return conta.getSaldoDisponivel();
    }
} 