package com.bancobr.repository;

import com.bancobr.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositório para entidade Log
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@Repository
public interface LogRepository extends JpaRepository<Log, UUID> {

    /**
     * Busca logs por usuário
     */
    List<Log> findByUsuarioId(UUID usuarioId);

    /**
     * Busca logs por ação
     */
    List<Log> findByAcao(String acao);

    /**
     * Busca logs por tabela
     */
    List<Log> findByTabela(String tabela);

    /**
     * Busca logs por registro ID
     */
    List<Log> findByRegistroId(UUID registroId);

    /**
     * Busca logs por usuário e ação
     */
    List<Log> findByUsuarioIdAndAcao(UUID usuarioId, String acao);

    /**
     * Busca logs por usuário e tabela
     */
    List<Log> findByUsuarioIdAndTabela(UUID usuarioId, String tabela);

    /**
     * Busca logs por ação e tabela
     */
    List<Log> findByAcaoAndTabela(String acao, String tabela);

    /**
     * Busca logs por data de criação após uma data
     */
    List<Log> findByCriadoEmAfter(LocalDateTime data);

    /**
     * Busca logs por data de criação antes de uma data
     */
    List<Log> findByCriadoEmBefore(LocalDateTime data);

    /**
     * Busca logs por data de criação entre duas datas
     */
    List<Log> findByCriadoEmBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Busca logs por usuário e período
     */
    List<Log> findByUsuarioIdAndCriadoEmBetween(UUID usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Busca logs por ação e período
     */
    List<Log> findByAcaoAndCriadoEmBetween(String acao, LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Busca logs por tabela e período
     */
    List<Log> findByTabelaAndCriadoEmBetween(String tabela, LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Busca logs por ação ordenados por data de criação
     */
    List<Log> findByAcaoOrderByCriadoEmDesc(String acao);

    /**
     * Busca logs por ação e tabela ordenados por data de criação
     */
    List<Log> findByAcaoAndTabelaOrderByCriadoEmDesc(String acao, String tabela);

    /**
     * Busca logs por usuário com ordenação
     */
    List<Log> findByUsuarioIdOrderByCriadoEmDesc(UUID usuarioId);

    /**
     * Busca logs por tabela com ordenação
     */
    List<Log> findByTabelaOrderByCriadoEmDesc(String tabela);

    /**
     * Busca logs por registro com ordenação
     */
    List<Log> findByRegistroIdOrderByCriadoEmDesc(UUID registroId);

    /**
     * Conta logs por usuário
     */
    long countByUsuarioId(UUID usuarioId);

    /**
     * Conta logs por ação
     */
    long countByAcao(String acao);

    /**
     * Conta logs por tabela
     */
    long countByTabela(String tabela);

    /**
     * Conta logs por período
     */
    long countByCriadoEmBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Conta logs por usuário e período
     */
    long countByUsuarioIdAndCriadoEmBetween(UUID usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Busca logs com usuário
     */
    @Query("SELECT l FROM Log l LEFT JOIN FETCH l.usuario WHERE l.id = :id")
    Log findByIdWithUsuario(@Param("id") UUID id);

    /**
     * Busca logs por usuário com dados do usuário
     */
    @Query("SELECT l FROM Log l LEFT JOIN FETCH l.usuario WHERE l.usuario.id = :usuarioId ORDER BY l.criadoEm DESC")
    List<Log> findByUsuarioIdWithUsuario(@Param("usuarioId") UUID usuarioId);

    /**
     * Busca logs por período com dados do usuário
     */
    @Query("SELECT l FROM Log l LEFT JOIN FETCH l.usuario WHERE l.criadoEm BETWEEN :dataInicio AND :dataFim ORDER BY l.criadoEm DESC")
    List<Log> findByCriadoEmBetweenWithUsuario(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    /**
     * Busca logs de auditoria por tabela
     */
    @Query("SELECT l FROM Log l LEFT JOIN FETCH l.usuario WHERE l.tabela = :tabela ORDER BY l.criadoEm DESC")
    List<Log> findByTabelaWithUsuario(@Param("tabela") String tabela);

    /**
     * Busca logs de auditoria por registro
     */
    @Query("SELECT l FROM Log l LEFT JOIN FETCH l.usuario WHERE l.registroId = :registroId ORDER BY l.criadoEm DESC")
    List<Log> findByRegistroIdWithUsuario(@Param("registroId") UUID registroId);

    /**
     * Busca logs de auditoria por ação
     */
    @Query("SELECT l FROM Log l LEFT JOIN FETCH l.usuario WHERE l.acao = :acao ORDER BY l.criadoEm DESC")
    List<Log> findByAcaoWithUsuario(@Param("acao") String acao);
} 