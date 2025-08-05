package com.bancobr.controller;

import com.bancobr.model.Conta;
import com.bancobr.model.Transacao;
import com.bancobr.model.Usuario;
import com.bancobr.service.ContaService;
import com.bancobr.service.JwtService;
import com.bancobr.service.TransacaoService;
import com.bancobr.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controlador para gerenciamento de contas bancárias
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@RestController
@RequestMapping("/contas")
@CrossOrigin(origins = "*")
public class ContaController {

    @Autowired
    private ContaService contaService;
    
    @Autowired
    private TransacaoService transacaoService;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Lista todas as contas (apenas ADMIN)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Conta>> listarTodas() {
        List<Conta> contas = contaService.listarTodas();
        return ResponseEntity.ok(contas);
    }

    /**
     * Busca conta por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        try {
            return contaService.buscarPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Busca conta por número
     */
    @GetMapping("/numero/{numeroConta}")
    public ResponseEntity<?> buscarPorNumero(@PathVariable String numeroConta) {
        try {
            return contaService.buscarPorNumero(numeroConta)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista contas por usuário
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable UUID usuarioId) {
        try {
            List<Conta> contas = contaService.listarPorUsuario(usuarioId);
            return ResponseEntity.ok(contas);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista contas do usuário atual (sem precisar do ID)
     */
    @GetMapping("/usuario")
    public ResponseEntity<?> listarContasUsuarioAtual(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extrair userId do token
            String token = authHeader.substring(7);
            String userId = jwtService.extractUserId(token);
            
            // Buscar contas do usuário
            List<Conta> contas = contaService.listarPorUsuario(UUID.fromString(userId));
            
            // Se não há contas, criar uma conta padrão para o usuário
            if (contas.isEmpty()) {
                System.out.println("🔍 [DEBUG] Usuário não possui contas, criando conta padrão...");
                
                // Buscar usuário
                Usuario usuario = usuarioRepository.findById(UUID.fromString(userId))
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
                
                // Criar conta padrão
                Conta contaPadrao = contaService.criarConta(
                    usuario.getId(),
                    "0001",
                    "12345678",
                    Conta.TipoConta.CORRENTE,
                    new BigDecimal("1000.00")
                );
                
                contas = List.of(contaPadrao);
                System.out.println("✅ [DEBUG] Conta padrão criada: " + contaPadrao.getId());
            }
            
            return ResponseEntity.ok(contas);
        } catch (Exception e) {
            System.out.println("❌ [DEBUG] Erro ao listar contas: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista contas por agência (apenas ADMIN)
     */
    @GetMapping("/agencia/{agencia}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarPorAgencia(@PathVariable String agencia) {
        try {
            List<Conta> contas = contaService.listarPorAgencia(agencia);
            return ResponseEntity.ok(contas);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista contas por tipo
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> listarPorTipo(@PathVariable String tipo) {
        try {
            Conta.TipoConta tipoConta = Conta.TipoConta.valueOf(tipo.toUpperCase());
            List<Conta> contas = contaService.listarPorTipo(tipoConta);
            return ResponseEntity.ok(contas);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista contas por usuário e tipo
     */
    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<?> listarPorUsuarioETipo(@PathVariable UUID usuarioId, @PathVariable String tipo) {
        try {
            Conta.TipoConta tipoConta = Conta.TipoConta.valueOf(tipo.toUpperCase());
            List<Conta> contas = contaService.listarPorUsuarioETipo(usuarioId, tipoConta);
            return ResponseEntity.ok(contas);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Cria uma nova conta (apenas ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarConta(@RequestBody Map<String, Object> request) {
        try {
            UUID usuarioId = UUID.fromString((String) request.get("usuarioId"));
            String agencia = (String) request.get("agencia");
            String numeroConta = (String) request.get("numeroConta");
            String tipo = (String) request.get("tipo");
            BigDecimal limiteCredito = new BigDecimal(request.get("limiteCredito").toString());

            Conta.TipoConta tipoConta = Conta.TipoConta.valueOf(tipo.toUpperCase());
            Conta conta = contaService.criarConta(usuarioId, agencia, numeroConta, tipoConta, limiteCredito);
            return ResponseEntity.ok(conta);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Atualiza limite de crédito da conta
     */
    @PutMapping("/{id}/limite")
    public ResponseEntity<?> atualizarLimite(@PathVariable UUID id, @RequestBody Map<String, Object> request) {
        try {
            BigDecimal novoLimite = new BigDecimal(request.get("limiteCredito").toString());
            Conta conta = contaService.atualizarLimiteCredito(id, novoLimite);
            return ResponseEntity.ok(conta);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Credita valor na conta
     */
    @PutMapping("/{id}/creditar")
    public ResponseEntity<?> creditar(@PathVariable UUID id, @RequestBody Map<String, Object> request) {
        try {
            BigDecimal valor = new BigDecimal(request.get("valor").toString());
            Conta conta = contaService.creditar(id, valor);
            return ResponseEntity.ok(conta);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Debita valor da conta
     */
    @PutMapping("/{id}/debitar")
    public ResponseEntity<?> debitar(@PathVariable UUID id, @RequestBody Map<String, Object> request) {
        try {
            BigDecimal valor = new BigDecimal(request.get("valor").toString());
            Conta conta = contaService.debitar(id, valor);
            return ResponseEntity.ok(conta);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Verifica se conta tem saldo suficiente
     */
    @GetMapping("/{id}/saldo-suficiente")
    public ResponseEntity<?> verificarSaldoSuficiente(@PathVariable UUID id, @RequestParam BigDecimal valor) {
        try {
            boolean temSaldo = contaService.temSaldoSuficiente(id, valor);
            Map<String, Object> response = Map.of("temSaldoSuficiente", temSaldo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista contas com saldo maior que valor
     */
    @GetMapping("/saldo-maior")
    public ResponseEntity<?> listarComSaldoMaior(@RequestParam BigDecimal valor) {
        try {
            List<Conta> contas = contaService.listarComSaldoMaiorQue(valor);
            return ResponseEntity.ok(contas);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista contas com saldo menor que valor
     */
    @GetMapping("/saldo-menor")
    public ResponseEntity<?> listarComSaldoMenor(@RequestParam BigDecimal valor) {
        try {
            List<Conta> contas = contaService.listarComSaldoMenorQue(valor);
            return ResponseEntity.ok(contas);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Conta contas por usuário
     */
    @GetMapping("/contar/usuario/{usuarioId}")
    public ResponseEntity<?> contarPorUsuario(@PathVariable UUID usuarioId) {
        try {
            long count = contaService.contarPorUsuario(usuarioId);
            Map<String, Object> response = Map.of("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Conta contas por tipo
     */
    @GetMapping("/contar/tipo/{tipo}")
    public ResponseEntity<?> contarPorTipo(@PathVariable String tipo) {
        try {
            Conta.TipoConta tipoConta = Conta.TipoConta.valueOf(tipo.toUpperCase());
            long count = contaService.contarPorTipo(tipoConta);
            Map<String, Object> response = Map.of("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Soma saldo total de todas as contas (apenas ADMIN)
     */
    @GetMapping("/saldo-total")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saldoTotal() {
        try {
            BigDecimal saldoTotal = contaService.somarSaldoTotal();
            Map<String, Object> response = Map.of("saldoTotal", saldoTotal);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Soma saldo por usuário
     */
    @GetMapping("/saldo/usuario/{usuarioId}")
    public ResponseEntity<?> saldoPorUsuario(@PathVariable UUID usuarioId) {
        try {
            BigDecimal saldo = contaService.somarSaldoPorUsuario(usuarioId);
            Map<String, Object> response = Map.of("saldo", saldo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Soma saldo por tipo
     */
    @GetMapping("/saldo/tipo/{tipo}")
    public ResponseEntity<?> saldoPorTipo(@PathVariable String tipo) {
        try {
            Conta.TipoConta tipoConta = Conta.TipoConta.valueOf(tipo.toUpperCase());
            BigDecimal saldo = contaService.somarSaldoPorTipo(tipoConta);
            Map<String, Object> response = Map.of("saldo", saldo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Busca conta com usuário
     */
    @GetMapping("/{id}/usuario")
    public ResponseEntity<?> buscarComUsuario(@PathVariable UUID id) {
        try {
            return contaService.buscarComUsuario(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Busca conta com transações
     */
    @GetMapping("/{id}/transacoes")
    public ResponseEntity<?> buscarComTransacoes(@PathVariable UUID id) {
        try {
            return contaService.buscarComTransacoes(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Dashboard de contas do usuário
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboardContas(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extrair userId do token
            String token = authHeader.substring(7);
            String userId = jwtService.extractUserId(token);
            
            // Buscar contas do usuário
            List<Conta> contas = contaService.listarPorUsuario(UUID.fromString(userId));
            
            // Calcular saldo total
            BigDecimal saldoTotal = contas.stream()
                .map(Conta::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Contar contas (todas são consideradas ativas por padrão)
            long contasAtivas = contas.size();
            
            // Buscar transações do usuário
            List<Transacao> transacoes = transacaoService.listarPorUsuario(UUID.fromString(userId));
            
            // Filtrar transações de hoje
            long transacoesHoje = transacoes.stream()
                .filter(t -> t.getRealizadaEm().toLocalDate().equals(java.time.LocalDate.now()))
                .count();
            
            // Pegar últimas 5 transações
            List<Transacao> ultimasTransacoes = transacoes.stream()
                .sorted((t1, t2) -> t2.getRealizadaEm().compareTo(t1.getRealizadaEm()))
                .limit(5)
                .collect(Collectors.toList());
            
            Map<String, Object> dashboard = Map.of(
                "saldoTotal", saldoTotal,
                "contasAtivas", contasAtivas,
                "transacoesHoje", transacoesHoje,
                "ultimasTransacoes", ultimasTransacoes.stream()
                    .map(t -> Map.of(
                        "id", t.getId().toString(),
                        "dataTransacao", t.getRealizadaEm().toString(),
                        "tipo", t.getTipo().toString(),
                        "valor", t.getValor(),
                        "status", "CONCLUIDA",
                        "descricao", t.getDescricao()
                    ))
                    .collect(Collectors.toList())
            );
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 