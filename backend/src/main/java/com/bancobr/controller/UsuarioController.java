package com.bancobr.controller;

import com.bancobr.model.Usuario;
import com.bancobr.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controlador para gerenciamento de usuários
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista todos os usuários (apenas ADMIN)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Lista usuários ativos (apenas ADMIN)
     */
    @GetMapping("/ativos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listarAtivos() {
        List<Usuario> usuarios = usuarioService.listarAtivos();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Busca usuário por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        try {
            return usuarioService.buscarPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Busca usuário por email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> buscarPorEmail(@PathVariable String email) {
        try {
            return usuarioService.buscarPorEmail(email)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Busca usuário por CPF
     */
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<?> buscarPorCpf(@PathVariable String cpf) {
        try {
            return usuarioService.buscarPorCpf(cpf)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista usuários por tipo (apenas ADMIN)
     */
    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarPorTipo(@PathVariable String tipo) {
        try {
            Usuario.TipoUsuario tipoUsuario = Usuario.TipoUsuario.valueOf(tipo.toUpperCase());
            List<Usuario> usuarios = usuarioService.listarPorTipo(tipoUsuario);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Busca usuários por nome
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorNome(@RequestParam String nome) {
        try {
            List<Usuario> usuarios = usuarioService.buscarPorNome(nome);
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Cria um novo usuário (apenas ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario usuarioCriado = usuarioService.criarUsuario(usuario);
            return ResponseEntity.ok(usuarioCriado);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Atualiza dados do usuário
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable UUID id, @RequestBody Usuario usuario) {
        try {
            Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, usuario);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Atualiza senha do usuário
     */
    @PutMapping("/{id}/senha")
    public ResponseEntity<?> atualizarSenha(@PathVariable UUID id, @RequestBody Map<String, String> request) {
        try {
            String novaSenha = request.get("novaSenha");
            if (novaSenha == null) {
                Map<String, Object> error = Map.of("error", "Nova senha é obrigatória");
                return ResponseEntity.badRequest().body(error);
            }

            usuarioService.atualizarSenha(id, novaSenha);
            Map<String, Object> response = Map.of("message", "Senha atualizada com sucesso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Altera status do usuário (apenas ADMIN)
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> alterarStatus(@PathVariable UUID id, @RequestBody Map<String, Boolean> request) {
        try {
            Boolean ativo = request.get("ativo");
            if (ativo == null) {
                Map<String, Object> error = Map.of("error", "Status é obrigatório");
                return ResponseEntity.badRequest().body(error);
            }

            Usuario usuario = usuarioService.alterarStatus(id, ativo);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Remove usuário (soft delete - apenas ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> removerUsuario(@PathVariable UUID id) {
        try {
            usuarioService.removerUsuario(id);
            Map<String, Object> response = Map.of("message", "Usuário removido com sucesso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Verifica se email existe
     */
    @GetMapping("/verificar/email/{email}")
    public ResponseEntity<?> verificarEmail(@PathVariable String email) {
        try {
            boolean existe = usuarioService.emailExiste(email);
            Map<String, Object> response = Map.of("existe", existe);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Verifica se CPF existe
     */
    @GetMapping("/verificar/cpf/{cpf}")
    public ResponseEntity<?> verificarCpf(@PathVariable String cpf) {
        try {
            boolean existe = usuarioService.cpfExiste(cpf);
            Map<String, Object> response = Map.of("existe", existe);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Conta usuários por tipo (apenas ADMIN)
     */
    @GetMapping("/contar/tipo/{tipo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> contarPorTipo(@PathVariable String tipo) {
        try {
            Usuario.TipoUsuario tipoUsuario = Usuario.TipoUsuario.valueOf(tipo.toUpperCase());
            long count = usuarioService.contarPorTipo(tipoUsuario);
            Map<String, Object> response = Map.of("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Conta usuários ativos (apenas ADMIN)
     */
    @GetMapping("/contar/ativos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> contarAtivos() {
        try {
            long count = usuarioService.contarAtivos();
            Map<String, Object> response = Map.of("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Busca usuário com contas
     */
    @GetMapping("/{id}/contas")
    public ResponseEntity<?> buscarComContas(@PathVariable UUID id) {
        try {
            return usuarioService.buscarComContas(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Busca usuário com logs (apenas ADMIN)
     */
    @GetMapping("/{id}/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> buscarComLogs(@PathVariable UUID id) {
        try {
            return usuarioService.buscarComLogs(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> error = Map.of("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 