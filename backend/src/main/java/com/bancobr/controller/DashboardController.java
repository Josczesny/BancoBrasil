package com.bancobr.controller;

import com.bancobr.model.Usuario;
import com.bancobr.service.ContaService;
import com.bancobr.service.TransacaoService;
import com.bancobr.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Controller para dashboard e relatórios
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ContaService contaService;

    @Autowired
    private TransacaoService transacaoService;

    /**
     * Dashboard geral (apenas ADMIN)
     */
    @GetMapping("/geral")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> dashboardGeral() {
        try {
            long totalUsuarios = usuarioService.contarAtivos();
            BigDecimal saldoTotal = contaService.somarSaldoTotal();

            Map<String, Object> dashboard = Map.of(
                "totalUsuarios", totalUsuarios,
                "saldoTotal", saldoTotal,
                "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Dashboard por usuário (ADMIN ou próprio usuário)
     */
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN') or #usuarioId == authentication.principal.userId")
    public ResponseEntity<?> dashboardUsuario(@PathVariable UUID usuarioId) {
        try {
            long totalContas = contaService.contarPorUsuario(usuarioId);
            long totalTransacoes = transacaoService.contarPorUsuario(usuarioId);
            BigDecimal saldoTotal = contaService.somarSaldoPorUsuario(usuarioId);

            Map<String, Object> dashboard = Map.of(
                "usuarioId", usuarioId,
                "totalContas", totalContas,
                "totalTransacoes", totalTransacoes,
                "saldoTotal", saldoTotal,
                "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Relatório de usuários por tipo (ADMIN)
     */
    @GetMapping("/relatorio/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> relatorioUsuarios() {
        try {
            long usuariosAdmin = usuarioService.contarPorTipo(Usuario.TipoUsuario.ADMIN);
            long usuariosCliente = usuarioService.contarPorTipo(Usuario.TipoUsuario.CLIENTE);

            Map<String, Object> relatorio = Map.of(
                "usuariosAdmin", usuariosAdmin,
                "usuariosCliente", usuariosCliente,
                "totalUsuarios", usuariosAdmin + usuariosCliente,
                "timestamp", LocalDateTime.now()
            );

            return ResponseEntity.ok(relatorio);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 