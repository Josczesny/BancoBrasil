package com.bancobr.repository;

import com.bancobr.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para entidade Usuario
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    /**
     * Busca usuário por email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca usuário por email e ativo
     */
    Optional<Usuario> findByEmailAndAtivoTrue(String email);

    /**
     * Busca usuário por CPF
     */
    Optional<Usuario> findByCpf(String cpf);

    /**
     * Verifica se existe usuário por email
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se existe usuário por CPF
     */
    boolean existsByCpf(String cpf);

    /**
     * Busca usuários por tipo
     */
    List<Usuario> findByTipo(Usuario.TipoUsuario tipo);

    /**
     * Busca usuários ativos
     */
    List<Usuario> findByAtivoTrue();

    /**
     * Busca usuários por tipo e ativo
     */
    List<Usuario> findByTipoAndAtivoTrue(Usuario.TipoUsuario tipo);

    /**
     * Busca usuários por nome contendo
     */
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca usuários criados após uma data
     */
    List<Usuario> findByCriadoEmAfter(LocalDateTime data);

    /**
     * Busca usuários criados entre duas datas
     */
    List<Usuario> findByCriadoEmBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Conta usuários por tipo
     */
    long countByTipo(Usuario.TipoUsuario tipo);

    /**
     * Conta usuários ativos
     */
    long countByAtivoTrue();

    /**
     * Busca usuário com contas
     */
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.contas WHERE u.id = :id")
    Optional<Usuario> findByIdWithContas(@Param("id") UUID id);

    /**
     * Busca usuário com logs
     */
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.logs WHERE u.id = :id")
    Optional<Usuario> findByIdWithLogs(@Param("id") UUID id);

    /**
     * Busca usuário com contas e logs
     */
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.contas LEFT JOIN FETCH u.logs WHERE u.id = :id")
    Optional<Usuario> findByIdWithContasAndLogs(@Param("id") UUID id);

    /**
     * Busca todos os usuários com contas
     */
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.contas")
    List<Usuario> findAllWithContas();

    /**
     * Busca usuários por tipo com contas
     */
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.contas WHERE u.tipo = :tipo")
    List<Usuario> findByTipoWithContas(@Param("tipo") Usuario.TipoUsuario tipo);
} 