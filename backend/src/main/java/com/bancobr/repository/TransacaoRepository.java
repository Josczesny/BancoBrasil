package com.bancobr.repository;

import com.bancobr.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para entidade Transacao
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, UUID> {

    /**
     * Busca transações por conta origem
     */
    List<Transacao> findByContaOrigemId(UUID contaOrigemId);

    /**
     * Busca transações por conta destino
     */
    List<Transacao> findByContaDestinoId(UUID contaDestinoId);

    /**
     * Busca transações por tipo
     */
    List<Transacao> findByTipo(Transacao.TipoTransacao tipo);

    /**
     * Busca transações por conta (origem ou destino)
     */
    @Query("SELECT t FROM Transacao t WHERE t.contaOrigem.id = :contaId OR t.contaDestino.id = :contaId")
    List<Transacao> findByContaId(@Param("contaId") UUID contaId);

    /**
     * Busca transações por usuário (através das contas)
     */
    @Query("SELECT t FROM Transacao t WHERE t.contaOrigem.usuario.id = :usuarioId OR t.contaDestino.usuario.id = :usuarioId")
    List<Transacao> findByUsuarioId(@Param("usuarioId") UUID usuarioId);

    /**
     * Busca transações por valor maior que
     */
    List<Transacao> findByValorGreaterThan(BigDecimal valor);

    /**
     * Busca transações por valor menor que
     */
    List<Transacao> findByValorLessThan(BigDecimal valor);

    /**
     * Busca transações por valor entre
     */
    List<Transacao> findByValorBetween(BigDecimal valorMin, BigDecimal valorMax);

    /**
     * Busca transações realizadas após uma data
     */
    List<Transacao> findByRealizadaEmAfter(LocalDateTime data);

    /**
     * Busca transações realizadas antes de uma data
     */
    List<Transacao> findByRealizadaEmBefore(LocalDateTime data);

    /**
     * Busca transações realizadas entre duas datas
     */
    List<Transacao> findByRealizadaEmBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Busca transações por tipo e data
     */
    List<Transacao> findByTipoAndRealizadaEmBetween(Transacao.TipoTransacao tipo, LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Busca transações por conta e tipo
     */
    @Query("SELECT t FROM Transacao t WHERE (t.contaOrigem.id = :contaId OR t.contaDestino.id = :contaId) AND t.tipo = :tipo")
    List<Transacao> findByContaIdAndTipo(@Param("contaId") UUID contaId, @Param("tipo") Transacao.TipoTransacao tipo);

    /**
     * Busca transações por usuário e tipo
     */
    @Query("SELECT t FROM Transacao t WHERE (t.contaOrigem.usuario.id = :usuarioId OR t.contaDestino.usuario.id = :usuarioId) AND t.tipo = :tipo")
    List<Transacao> findByUsuarioIdAndTipo(@Param("usuarioId") UUID usuarioId, @Param("tipo") Transacao.TipoTransacao tipo);

    /**
     * Busca transações pendentes
     */
    @Query("SELECT t FROM Transacao t WHERE t.contaOrigem IS NULL OR t.contaDestino IS NULL")
    List<Transacao> findTransacoesPendentes();

    /**
     * Busca transações processadas
     */
    @Query("SELECT t FROM Transacao t WHERE t.contaOrigem IS NOT NULL AND t.contaDestino IS NOT NULL")
    List<Transacao> findTransacoesProcessadas();

    /**
     * Soma valor total de transações
     */
    @Query("SELECT SUM(t.valor) FROM Transacao t")
    BigDecimal sumValorTotal();

    /**
     * Soma valor por tipo
     */
    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.tipo = :tipo")
    BigDecimal sumValorByTipo(@Param("tipo") Transacao.TipoTransacao tipo);

    /**
     * Soma valor por conta
     */
    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.contaOrigem.id = :contaId OR t.contaDestino.id = :contaId")
    BigDecimal sumValorByContaId(@Param("contaId") UUID contaId);

    /**
     * Soma valor por usuário
     */
    @Query("SELECT SUM(t.valor) FROM Transacao t WHERE t.contaOrigem.usuario.id = :usuarioId OR t.contaDestino.usuario.id = :usuarioId")
    BigDecimal sumValorByUsuarioId(@Param("usuarioId") UUID usuarioId);

    /**
     * Conta transações por tipo
     */
    long countByTipo(Transacao.TipoTransacao tipo);

    /**
     * Conta transações por conta
     */
    @Query("SELECT COUNT(t) FROM Transacao t WHERE t.contaOrigem.id = :contaId OR t.contaDestino.id = :contaId")
    long countByContaId(@Param("contaId") UUID contaId);

    /**
     * Conta transações por usuário
     */
    @Query("SELECT COUNT(t) FROM Transacao t WHERE t.contaOrigem.usuario.id = :usuarioId OR t.contaDestino.usuario.id = :usuarioId")
    long countByUsuarioId(@Param("usuarioId") UUID usuarioId);

    /**
     * Busca última transação por conta
     */
    @Query("SELECT t FROM Transacao t WHERE t.contaOrigem.id = :contaId OR t.contaDestino.id = :contaId ORDER BY t.realizadaEm DESC")
    List<Transacao> findUltimasTransacoesByContaId(@Param("contaId") UUID contaId);

    /**
     * Busca transação com contas
     */
    @Query("SELECT t FROM Transacao t LEFT JOIN FETCH t.contaOrigem LEFT JOIN FETCH t.contaDestino WHERE t.id = :id")
    Optional<Transacao> findByIdWithContas(@Param("id") UUID id);
} 