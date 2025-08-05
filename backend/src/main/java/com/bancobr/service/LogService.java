package com.bancobr.service;

import com.bancobr.model.Log;
import com.bancobr.model.Usuario;
import com.bancobr.repository.LogRepository;
import com.bancobr.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço para gerenciamento de logs de auditoria
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@Service
@Transactional
public class LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Cria um novo log
     */
    public Log criarLog(Usuario usuario, String acao, String tabela, UUID registroId) {
        Log log = new Log(usuario, acao, tabela, registroId);
        // Não define explicitamente os campos JSON como null
        return logRepository.save(log);
    }

    /**
     * Cria um novo log com dados de auditoria
     */
    public Log criarLogComAuditoria(Usuario usuario, String acao, String tabela, UUID registroId, 
                                   String dadosAnteriores, String dadosNovos) {
        Log log = new Log(usuario, acao, tabela, registroId);
        log.setDadosAnteriores(dadosAnteriores);
        log.setDadosNovos(dadosNovos);
        return logRepository.save(log);
    }

    /**
     * Cria um novo log sem usuário (para operações do sistema)
     */
    public Log criarLogSistema(String acao, String tabela, UUID registroId) {
        Log log = new Log(null, acao, tabela, registroId);
        return logRepository.save(log);
    }

    /**
     * Cria um novo log do sistema com dados de auditoria
     */
    public Log criarLogSistemaComAuditoria(String acao, String tabela, UUID registroId, 
                                          String dadosAnteriores, String dadosNovos) {
        Log log = new Log(null, acao, tabela, registroId);
        log.setDadosAnteriores(dadosAnteriores);
        log.setDadosNovos(dadosNovos);
        return logRepository.save(log);
    }

    /**
     * Busca log por ID
     */
    public Optional<Log> buscarPorId(UUID id) {
        return logRepository.findById(id);
    }

    /**
     * Busca log com usuário
     */
    public Log buscarComUsuario(UUID id) {
        return logRepository.findByIdWithUsuario(id);
    }

    /**
     * Lista logs por usuário
     */
    public List<Log> listarPorUsuario(UUID usuarioId) {
        return logRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Lista logs por usuário com dados do usuário
     */
    public List<Log> listarPorUsuarioComDados(UUID usuarioId) {
        return logRepository.findByUsuarioIdWithUsuario(usuarioId);
    }

    /**
     * Lista logs por ação
     */
    public List<Log> listarPorAcao(String acao) {
        return logRepository.findByAcao(acao);
    }

    /**
     * Lista logs por tabela
     */
    public List<Log> listarPorTabela(String tabela) {
        return logRepository.findByTabela(tabela);
    }

    /**
     * Lista logs por registro
     */
    public List<Log> listarPorRegistro(UUID registroId) {
        return logRepository.findByRegistroId(registroId);
    }

    /**
     * Lista logs por período
     */
    public List<Log> listarPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return logRepository.findByCriadoEmBetween(dataInicio, dataFim);
    }

    /**
     * Lista logs por período com dados do usuário
     */
    public List<Log> listarPorPeriodoComDados(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return logRepository.findByCriadoEmBetweenWithUsuario(dataInicio, dataFim);
    }

    /**
     * Lista logs por usuário e período
     */
    public List<Log> listarPorUsuarioEPeriodo(UUID usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return logRepository.findByUsuarioIdAndCriadoEmBetween(usuarioId, dataInicio, dataFim);
    }

    /**
     * Lista logs por ação e período
     */
    public List<Log> listarPorAcaoEPeriodo(String acao, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return logRepository.findByAcaoAndCriadoEmBetween(acao, dataInicio, dataFim);
    }

    /**
     * Lista logs por tabela e período
     */
    public List<Log> listarPorTabelaEPeriodo(String tabela, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return logRepository.findByTabelaAndCriadoEmBetween(tabela, dataInicio, dataFim);
    }

    /**
     * Lista logs por usuário e ação
     */
    public List<Log> listarPorUsuarioEAcao(UUID usuarioId, String acao) {
        return logRepository.findByUsuarioIdAndAcao(usuarioId, acao);
    }

    /**
     * Lista logs por usuário e tabela
     */
    public List<Log> listarPorUsuarioETabela(UUID usuarioId, String tabela) {
        return logRepository.findByUsuarioIdAndTabela(usuarioId, tabela);
    }

    /**
     * Lista logs por ação e tabela
     */
    public List<Log> listarPorAcaoETabela(String acao, String tabela) {
        return logRepository.findByAcaoAndTabela(acao, tabela);
    }

    /**
     * Lista logs ordenados por data de criação
     */
    public List<Log> listarOrdenadosPorData() {
        return logRepository.findByAcaoOrderByCriadoEmDesc("INSERT");
    }

    /**
     * Lista logs por usuário ordenados
     */
    public List<Log> listarPorUsuarioOrdenados(UUID usuarioId) {
        return logRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId);
    }

    /**
     * Lista logs por tabela ordenados
     */
    public List<Log> listarPorTabelaOrdenados(String tabela) {
        return logRepository.findByTabelaOrderByCriadoEmDesc(tabela);
    }

    /**
     * Lista logs por registro ordenados
     */
    public List<Log> listarPorRegistroOrdenados(UUID registroId) {
        return logRepository.findByRegistroIdOrderByCriadoEmDesc(registroId);
    }

    /**
     * Lista logs de auditoria por tabela
     */
    public List<Log> listarAuditoriaPorTabela(String tabela) {
        return logRepository.findByTabelaWithUsuario(tabela);
    }

    /**
     * Lista logs de auditoria por registro
     */
    public List<Log> listarAuditoriaPorRegistro(UUID registroId) {
        return logRepository.findByRegistroIdWithUsuario(registroId);
    }

    /**
     * Lista logs de auditoria por ação
     */
    public List<Log> listarAuditoriaPorAcao(String acao) {
        return logRepository.findByAcaoWithUsuario(acao);
    }

    /**
     * Conta logs por usuário
     */
    public long contarPorUsuario(UUID usuarioId) {
        return logRepository.countByUsuarioId(usuarioId);
    }

    /**
     * Conta logs por ação
     */
    public long contarPorAcao(String acao) {
        return logRepository.countByAcao(acao);
    }

    /**
     * Conta logs por tabela
     */
    public long contarPorTabela(String tabela) {
        return logRepository.countByTabela(tabela);
    }

    /**
     * Conta logs por período
     */
    public long contarPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return logRepository.countByCriadoEmBetween(dataInicio, dataFim);
    }

    /**
     * Conta logs por usuário e período
     */
    public long contarPorUsuarioEPeriodo(UUID usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        return logRepository.countByUsuarioIdAndCriadoEmBetween(usuarioId, dataInicio, dataFim);
    }

    /**
     * Lista todos os logs
     */
    public List<Log> listarTodos() {
        return logRepository.findAll();
    }

    /**
     * Lista logs de inserções
     */
    public List<Log> listarInsercoes() {
        return logRepository.findByAcao("INSERT");
    }

    /**
     * Lista logs de atualizações
     */
    public List<Log> listarAtualizacoes() {
        return logRepository.findByAcao("UPDATE");
    }

    /**
     * Lista logs de exclusões
     */
    public List<Log> listarExclusoes() {
        return logRepository.findByAcao("DELETE");
    }

    /**
     * Lista logs de inserções ordenados
     */
    public List<Log> listarInsercoesOrdenadas() {
        return logRepository.findByAcaoOrderByCriadoEmDesc("INSERT");
    }

    /**
     * Lista logs de atualizações ordenados
     */
    public List<Log> listarAtualizacoesOrdenadas() {
        return logRepository.findByAcaoOrderByCriadoEmDesc("UPDATE");
    }

    /**
     * Lista logs de exclusões ordenados
     */
    public List<Log> listarExclusoesOrdenadas() {
        return logRepository.findByAcaoOrderByCriadoEmDesc("DELETE");
    }

    /**
     * Obtém relatório de auditoria por período
     */
    public List<Log> obterRelatorioAuditoria(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return logRepository.findByCriadoEmBetweenWithUsuario(dataInicio, dataFim);
    }

    /**
     * Obtém relatório de auditoria por usuário
     */
    public List<Log> obterRelatorioAuditoriaPorUsuario(UUID usuarioId) {
        return logRepository.findByUsuarioIdWithUsuario(usuarioId);
    }

    /**
     * Obtém relatório de auditoria por tabela
     */
    public List<Log> obterRelatorioAuditoriaPorTabela(String tabela) {
        return logRepository.findByTabelaWithUsuario(tabela);
    }

    /**
     * Obtém relatório de auditoria por ação
     */
    public List<Log> obterRelatorioAuditoriaPorAcao(String acao) {
        return logRepository.findByAcaoWithUsuario(acao);
    }
} 