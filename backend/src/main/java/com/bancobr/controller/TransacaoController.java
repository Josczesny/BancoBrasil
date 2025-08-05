package com.bancobr.controller;

import com.bancobr.model.Transacao;
import com.bancobr.service.TransacaoService;
import com.bancobr.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador para gerenciamento de transações bancárias
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@RestController
@RequestMapping("/transacoes")
@CrossOrigin(origins = "*")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;
    
    @Autowired
    private JwtService jwtService;

    /**
     * Realiza transferência entre contas
     */
    @PostMapping("/transferencia")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<?> realizarTransferencia(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("🔍 [DEBUG] Request recebido: " + request);
            
            // Validação dos campos obrigatórios
            if (!request.containsKey("contaOrigem") || request.get("contaOrigem") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "contaOrigem é obrigatório"));
            }
            if (!request.containsKey("contaDestino") || request.get("contaDestino") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "contaDestino é obrigatório"));
            }
            if (!request.containsKey("valor") || request.get("valor") == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "valor é obrigatório"));
            }
            
            String numeroContaOrigem = (String) request.get("contaOrigem");
            String numeroContaDestino = (String) request.get("contaDestino");
            
            // Tratar valor como string primeiro para evitar problemas de conversão
            Object valorObj = request.get("valor");
            BigDecimal valor;
            if (valorObj instanceof String) {
                valor = new BigDecimal((String) valorObj);
            } else if (valorObj instanceof Number) {
                valor = new BigDecimal(valorObj.toString());
            } else {
                throw new RuntimeException("Valor inválido");
            }
            
            String descricao = (String) request.get("descricao");
            
            // Se descrição for null, define uma descrição padrão
            if (descricao == null || descricao.trim().isEmpty()) {
                descricao = "Transferência entre contas";
            }

            System.out.println("🔍 [DEBUG] Dados processados:");
            System.out.println("   - Conta Origem: " + numeroContaOrigem);
            System.out.println("   - Conta Destino: " + numeroContaDestino);
            System.out.println("   - Valor: " + valor);
            System.out.println("   - Descrição: " + descricao);

            Transacao transacao = transacaoService.realizarTransferenciaPorNumero(numeroContaOrigem, numeroContaDestino, valor, descricao);
            return ResponseEntity.ok(transacao);
        } catch (Exception e) {
            System.out.println("❌ [DEBUG] Erro na transferência: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Realiza depósito
     */
    @PostMapping("/deposito")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<?> realizarDeposito(@RequestBody Map<String, Object> request) {
        try {
            UUID contaDestinoId = UUID.fromString((String) request.get("contaDestinoId"));
            BigDecimal valor = new BigDecimal(request.get("valor").toString());
            String descricao = (String) request.get("descricao");

            Transacao transacao = transacaoService.realizarDeposito(contaDestinoId, valor, descricao);
            return ResponseEntity.ok(transacao);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Realiza saque
     */
    @PostMapping("/saque")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    public ResponseEntity<?> realizarSaque(@RequestBody Map<String, Object> request) {
        try {
            UUID contaOrigemId = UUID.fromString((String) request.get("contaOrigemId"));
            BigDecimal valor = new BigDecimal(request.get("valor").toString());
            String descricao = (String) request.get("descricao");

            Transacao transacao = transacaoService.realizarSaque(contaOrigemId, valor, descricao);
            return ResponseEntity.ok(transacao);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista todas as transações (apenas ADMIN)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Transacao>> listarTodas() {
        List<Transacao> transacoes = transacaoService.listarProcessadas();
        return ResponseEntity.ok(transacoes);
    }

    /**
     * Busca transação por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        try {
            return transacaoService.buscarPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista transações por conta
     */
    @GetMapping("/conta/{contaId}")
    public ResponseEntity<?> listarPorConta(@PathVariable UUID contaId) {
        try {
            List<Transacao> transacoes = transacaoService.listarPorConta(contaId);
            return ResponseEntity.ok(transacoes);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista transações por usuário
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable UUID usuarioId) {
        try {
            List<Transacao> transacoes = transacaoService.listarPorUsuario(usuarioId);
            return ResponseEntity.ok(transacoes);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista transações do usuário atual (sem precisar do ID)
     */
    @GetMapping("/usuario")
    public ResponseEntity<?> listarTransacoesUsuarioAtual(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extrair userId do token
            String token = authHeader.substring(7);
            String userId = jwtService.extractUserId(token);
            
            // Buscar transações do usuário
            List<Transacao> transacoes = transacaoService.listarPorUsuario(UUID.fromString(userId));
            return ResponseEntity.ok(transacoes);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista transações por tipo
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> listarPorTipo(@PathVariable String tipo) {
        try {
            Transacao.TipoTransacao tipoTransacao = Transacao.TipoTransacao.valueOf(tipo.toUpperCase());
            List<Transacao> transacoes = transacaoService.listarPorTipo(tipoTransacao);
            return ResponseEntity.ok(transacoes);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista transações por período
     */
    @GetMapping("/periodo")
    public ResponseEntity<?> listarPorPeriodo(@RequestParam String dataInicio, @RequestParam String dataFim) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(dataInicio);
            LocalDateTime fim = LocalDateTime.parse(dataFim);
            List<Transacao> transacoes = transacaoService.listarPorPeriodo(inicio, fim);
            return ResponseEntity.ok(transacoes);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista transações por conta e tipo
     */
    @GetMapping("/conta/{contaId}/tipo/{tipo}")
    public ResponseEntity<?> listarPorContaETipo(@PathVariable UUID contaId, @PathVariable String tipo) {
        try {
            Transacao.TipoTransacao tipoTransacao = Transacao.TipoTransacao.valueOf(tipo.toUpperCase());
            List<Transacao> transacoes = transacaoService.listarPorContaETipo(contaId, tipoTransacao);
            return ResponseEntity.ok(transacoes);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista transações por usuário e tipo
     */
    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<?> listarPorUsuarioETipo(@PathVariable UUID usuarioId, @PathVariable String tipo) {
        try {
            Transacao.TipoTransacao tipoTransacao = Transacao.TipoTransacao.valueOf(tipo.toUpperCase());
            List<Transacao> transacoes = transacaoService.listarPorUsuarioETipo(usuarioId, tipoTransacao);
            return ResponseEntity.ok(transacoes);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Obtém extrato da conta
     */
    @GetMapping("/extrato/conta/{contaId}")
    public ResponseEntity<?> obterExtratoConta(@PathVariable UUID contaId) {
        try {
            List<Transacao> extrato = transacaoService.obterExtrato(contaId);
            return ResponseEntity.ok(extrato);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Obtém extrato do usuário
     */
    @GetMapping("/extrato/usuario/{usuarioId}")
    public ResponseEntity<?> obterExtratoUsuario(@PathVariable UUID usuarioId) {
        try {
            List<Transacao> extrato = transacaoService.obterExtratoPorUsuario(usuarioId);
            return ResponseEntity.ok(extrato);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Obtém extrato por período
     */
    @GetMapping("/extrato/periodo")
    public ResponseEntity<?> obterExtratoPorPeriodo(@RequestParam UUID contaId, @RequestParam String dataInicio, @RequestParam String dataFim) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(dataInicio);
            LocalDateTime fim = LocalDateTime.parse(dataFim);
            List<Transacao> extrato = transacaoService.obterExtratoPorPeriodo(contaId, inicio, fim);
            return ResponseEntity.ok(extrato);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Conta transações por tipo
     */
    @GetMapping("/contar/tipo/{tipo}")
    public ResponseEntity<?> contarPorTipo(@PathVariable String tipo) {
        try {
            Transacao.TipoTransacao tipoTransacao = Transacao.TipoTransacao.valueOf(tipo.toUpperCase());
            long count = transacaoService.contarPorTipo(tipoTransacao);
            Map<String, Object> response = Map.of("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Conta transações por conta
     */
    @GetMapping("/contar/conta/{contaId}")
    public ResponseEntity<?> contarPorConta(@PathVariable UUID contaId) {
        try {
            long count = transacaoService.contarPorConta(contaId);
            Map<String, Object> response = Map.of("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Conta transações por usuário
     */
    @GetMapping("/contar/usuario/{usuarioId}")
    public ResponseEntity<?> contarPorUsuario(@PathVariable UUID usuarioId) {
        try {
            long count = transacaoService.contarPorUsuario(usuarioId);
            Map<String, Object> response = Map.of("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Soma valor total das transações (apenas ADMIN)
     */
    @GetMapping("/valor-total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> valorTotal() {
        try {
            BigDecimal valorTotal = transacaoService.somarValorTotal();
            Map<String, Object> response = Map.of("valorTotal", valorTotal);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Soma valor por tipo
     */
    @GetMapping("/valor/tipo/{tipo}")
    public ResponseEntity<?> valorPorTipo(@PathVariable String tipo) {
        try {
            Transacao.TipoTransacao tipoTransacao = Transacao.TipoTransacao.valueOf(tipo.toUpperCase());
            BigDecimal valor = transacaoService.somarValorPorTipo(tipoTransacao);
            Map<String, Object> response = Map.of("valor", valor);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Soma valor por conta
     */
    @GetMapping("/valor/conta/{contaId}")
    public ResponseEntity<?> valorPorConta(@PathVariable UUID contaId) {
        try {
            BigDecimal valor = transacaoService.somarValorPorConta(contaId);
            Map<String, Object> response = Map.of("valor", valor);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Soma valor por usuário
     */
    @GetMapping("/valor/usuario/{usuarioId}")
    public ResponseEntity<?> valorPorUsuario(@PathVariable UUID usuarioId) {
        try {
            BigDecimal valor = transacaoService.somarValorPorUsuario(usuarioId);
            Map<String, Object> response = Map.of("valor", valor);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Busca transação com contas
     */
    @GetMapping("/{id}/contas")
    public ResponseEntity<?> buscarComContas(@PathVariable UUID id) {
        try {
            return transacaoService.buscarComContas(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 