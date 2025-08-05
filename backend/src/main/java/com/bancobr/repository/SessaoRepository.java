package com.bancobr.repository;

import com.bancobr.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para entidade Sessao
 */
@Repository
public interface SessaoRepository extends JpaRepository<Sessao, UUID> {

    /**
     * Busca sessão por token
     */
    Optional<Sessao> findByToken(String token);

    /**
     * Busca sessão por token e ativa
     */
    Optional<Sessao> findByTokenAndAtivaTrue(String token);

    /**
     * Busca sessões por usuário
     */
    List<Sessao> findByUsuarioId(UUID usuarioId);

    /**
     * Busca sessões ativas por usuário
     */
    List<Sessao> findByUsuarioIdAndAtivaTrue(UUID usuarioId);

    /**
     * Busca sessões expiradas
     */
    @Query("SELECT s FROM Sessao s WHERE s.expiraEm < :agora AND s.ativa = true")
    List<Sessao> findSessoesExpiradas(@Param("agora") LocalDateTime agora);

    /**
     * Busca sessões por usuário e ativa
     */
    @Query("SELECT s FROM Sessao s WHERE s.usuario.id = :usuarioId AND s.ativa = true ORDER BY s.criadaEm DESC")
    List<Sessao> findSessoesAtivasPorUsuario(@Param("usuarioId") UUID usuarioId);

    /**
     * Conta sessões ativas por usuário
     */
    @Query("SELECT COUNT(s) FROM Sessao s WHERE s.usuario.id = :usuarioId AND s.ativa = true")
    long countSessoesAtivasPorUsuario(@Param("usuarioId") UUID usuarioId);

    /**
     * Remove sessões expiradas
     */
    @Query("DELETE FROM Sessao s WHERE s.expiraEm < :agora")
    void deleteSessoesExpiradas(@Param("agora") LocalDateTime agora);

    /**
     * Busca sessões criadas após uma data
     */
    @Query("SELECT s FROM Sessao s WHERE s.criadaEm >= :dataInicio ORDER BY s.criadaEm DESC")
    List<Sessao> findByCriadaEmAfter(@Param("dataInicio") LocalDateTime dataInicio);

    /**
     * Busca sessões por período de expiração
     */
    @Query("SELECT s FROM Sessao s WHERE s.expiraEm BETWEEN :dataInicio AND :dataFim AND s.ativa = true")
    List<Sessao> findByExpiraEmBetween(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);
} 