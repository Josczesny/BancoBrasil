package com.bancobr.controller;

import com.bancobr.model.Log;
import com.bancobr.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller para gerenciamento de logs de auditoria
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * Endpoint de teste (apenas ADMIN)
     */
    @GetMapping("/teste")
    public ResponseEntity<?> teste() {
        return ResponseEntity.ok(Map.of("message", "LogController funcionando!", "timestamp", LocalDateTime.now()));
    }

    /**
     * Lista todos os logs (apenas ADMIN)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarTodos() {
        try {
            List<Log> logs = logService.listarTodos();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista logs por usuário (apenas ADMIN)
     */
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarPorUsuario(@PathVariable UUID usuarioId) {
        try {
            List<Log> logs = logService.listarPorUsuario(usuarioId);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista logs por ação (apenas ADMIN)
     */
    @GetMapping("/acao/{acao}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarPorAcao(@PathVariable String acao) {
        try {
            List<Log> logs = logService.listarPorAcao(acao);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista logs por tabela (apenas ADMIN)
     */
    @GetMapping("/tabela/{tabela}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarPorTabela(@PathVariable String tabela) {
        try {
            List<Log> logs = logService.listarPorTabela(tabela);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista logs por período (apenas ADMIN)
     */
    @GetMapping("/periodo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        try {
            List<Log> logs = logService.listarPorPeriodo(dataInicio, dataFim);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista logs de inserções (apenas ADMIN)
     */
    @GetMapping("/insercoes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarInsercoes() {
        try {
            List<Log> logs = logService.listarInsercoes();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista logs de atualizações (apenas ADMIN)
     */
    @GetMapping("/atualizacoes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarAtualizacoes() {
        try {
            List<Log> logs = logService.listarAtualizacoes();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista logs de exclusões (apenas ADMIN)
     */
    @GetMapping("/exclusoes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarExclusoes() {
        try {
            List<Log> logs = logService.listarExclusoes();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Conta logs por usuário (apenas ADMIN)
     */
    @GetMapping("/contar/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> contarPorUsuario(@PathVariable UUID usuarioId) {
        try {
            long count = logService.contarPorUsuario(usuarioId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Conta logs por ação (apenas ADMIN)
     */
    @GetMapping("/contar/acao/{acao}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> contarPorAcao(@PathVariable String acao) {
        try {
            long count = logService.contarPorAcao(acao);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Conta logs por período (apenas ADMIN)
     */
    @GetMapping("/contar/periodo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> contarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        try {
            long count = logService.contarPorPeriodo(dataInicio, dataFim);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista logs de auditoria por usuário (apenas ADMIN)
     */
    @GetMapping("/auditoria/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarAuditoriaPorUsuario(@PathVariable UUID usuarioId) {
        try {
            List<Log> logs = logService.obterRelatorioAuditoriaPorUsuario(usuarioId);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista logs de auditoria por tabela (apenas ADMIN)
     */
    @GetMapping("/auditoria/tabela/{tabela}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarAuditoriaPorTabela(@PathVariable String tabela) {
        try {
            List<Log> logs = logService.obterRelatorioAuditoriaPorTabela(tabela);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista logs de auditoria por ação (apenas ADMIN)
     */
    @GetMapping("/auditoria/acao/{acao}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarAuditoriaPorAcao(@PathVariable String acao) {
        try {
            List<Log> logs = logService.obterRelatorioAuditoriaPorAcao(acao);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista logs de auditoria por período (apenas ADMIN)
     */
    @GetMapping("/auditoria/periodo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarAuditoriaPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        try {
            List<Log> logs = logService.obterRelatorioAuditoria(dataInicio, dataFim);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 