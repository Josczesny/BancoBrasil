package com.bancobr.repository;

import com.bancobr.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para entidade Conta
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@Repository
public interface ContaRepository extends JpaRepository<Conta, UUID> {

    /**
     * Busca conta por número
     */
    Optional<Conta> findByNumeroConta(String numeroConta);

    /**
     * Busca contas por usuário
     */
    List<Conta> findByUsuarioId(UUID usuarioId);

    /**
     * Busca contas por agência
     */
    List<Conta> findByAgencia(String agencia);

    /**
     * Busca contas por tipo
     */
    List<Conta> findByTipo(Conta.TipoConta tipo);

    /**
     * Busca contas por usuário e tipo
     */
    List<Conta> findByUsuarioIdAndTipo(UUID usuarioId, Conta.TipoConta tipo);

    /**
     * Busca contas com saldo maior que
     */
    List<Conta> findBySaldoGreaterThan(BigDecimal saldo);

    /**
     * Busca contas com saldo menor que
     */
    List<Conta> findBySaldoLessThan(BigDecimal saldo);

    /**
     * Verifica se existe conta por número
     */
    boolean existsByNumeroConta(String numeroConta);

    /**
     * Conta contas por usuário
     */
    long countByUsuarioId(UUID usuarioId);

    /**
     * Conta contas por tipo
     */
    long countByTipo(Conta.TipoConta tipo);

    /**
     * Busca conta com usuário
     */
    @Query("SELECT c FROM Conta c LEFT JOIN FETCH c.usuario WHERE c.id = :id")
    Optional<Conta> findByIdWithUsuario(@Param("id") UUID id);

    /**
     * Busca conta com transações
     */
    @Query("SELECT c FROM Conta c LEFT JOIN FETCH c.transacoesOrigem LEFT JOIN FETCH c.transacoesDestino WHERE c.id = :id")
    Optional<Conta> findByIdWithTransacoes(@Param("id") UUID id);

    /**
     * Busca conta com usuário e transações
     */
    @Query("SELECT c FROM Conta c LEFT JOIN FETCH c.usuario LEFT JOIN FETCH c.transacoesOrigem LEFT JOIN FETCH c.transacoesDestino WHERE c.id = :id")
    Optional<Conta> findByIdWithUsuarioAndTransacoes(@Param("id") UUID id);

    /**
     * Busca contas por usuário com transações
     */
    @Query("SELECT c FROM Conta c LEFT JOIN FETCH c.transacoesOrigem LEFT JOIN FETCH c.transacoesDestino WHERE c.usuario.id = :usuarioId")
    List<Conta> findByUsuarioIdWithTransacoes(@Param("usuarioId") UUID usuarioId);

    /**
     * Soma saldo total de todas as contas
     */
    @Query("SELECT SUM(c.saldo) FROM Conta c")
    BigDecimal sumSaldoTotal();

    /**
     * Soma saldo por usuário
     */
    @Query("SELECT SUM(c.saldo) FROM Conta c WHERE c.usuario.id = :usuarioId")
    BigDecimal sumSaldoByUsuarioId(@Param("usuarioId") UUID usuarioId);

    /**
     * Soma saldo por tipo
     */
    @Query("SELECT SUM(c.saldo) FROM Conta c WHERE c.tipo = :tipo")
    BigDecimal sumSaldoByTipo(@Param("tipo") Conta.TipoConta tipo);

    /**
     * Conta contas ativas
     */
    @Query("SELECT COUNT(c) FROM Conta c")
    long countContasAtivas();

    /**
     * Busca contas com saldo disponível maior que
     */
    @Query("SELECT c FROM Conta c WHERE (c.saldo + c.limiteCredito) > :valor")
    List<Conta> findBySaldoDisponivelGreaterThan(@Param("valor") BigDecimal valor);
} 