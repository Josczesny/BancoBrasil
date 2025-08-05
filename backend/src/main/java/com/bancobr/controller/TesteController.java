package com.bancobr.controller;

import com.bancobr.model.Usuario;
import com.bancobr.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Controller de teste simples
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@RestController
@RequestMapping("/teste")
public class TesteController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostConstruct
    public void init() {
        System.out.println("✅ TesteController carregado com sucesso!");
    }

    /**
     * Endpoint de teste simples
     */
    @GetMapping("/simples")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> testeSimples() {
        return ResponseEntity.ok(Map.of(
            "message", "TesteController funcionando!",
            "timestamp", LocalDateTime.now()
        ));
    }

    /**
     * Endpoint de teste com autenticação
     */
    @GetMapping("/auth")
    public ResponseEntity<?> testeAuth() {
        return ResponseEntity.ok(Map.of(
            "message", "TesteController com auth funcionando!",
            "timestamp", LocalDateTime.now()
        ));
    }

    /**
     * Endpoint para verificar usuários no banco
     */
    @GetMapping("/usuarios")
    public ResponseEntity<?> verificarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            return ResponseEntity.ok(Map.of(
                "total", usuarios.size(),
                "usuarios", usuarios.stream().map(u -> Map.of(
                    "id", u.getId(),
                    "nome", u.getNome(),
                    "email", u.getEmail(),
                    "tipo", u.getTipo(),
                    "ativo", u.getAtivo(),
                    "senha_hash", u.getSenhaHash().substring(0, 10) + "..."
                )).toList()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Endpoint para gerar senhas BCrypt válidas
     */
    @GetMapping("/gerar-senhas")
    public ResponseEntity<?> gerarSenhas() {
        try {
            org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder = 
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            
            String admin123 = encoder.encode("admin123");
            String cliente123 = encoder.encode("cliente123");
            String cliente456 = encoder.encode("cliente456");
            String cliente789 = encoder.encode("cliente789");
            String cliente012 = encoder.encode("cliente012");
            
            return ResponseEntity.ok(Map.of(
                "admin123", admin123,
                "cliente123", cliente123,
                "cliente456", cliente456,
                "cliente789", cliente789,
                "cliente012", cliente012
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Endpoint para testar validação de senha
     */
    @GetMapping("/teste-senha")
    public ResponseEntity<Map<String, Object>> testarSenha() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Busca o usuário admin
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndAtivoTrue("admin@bancobr.com");
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                
                // Testa a senha
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                boolean senhaValida = encoder.matches("admin123", usuario.getSenhaHash());
                
                response.put("usuario_encontrado", true);
                response.put("email", usuario.getEmail());
                response.put("senha_hash", usuario.getSenhaHash());
                response.put("senha_valida", senhaValida);
                response.put("ativo", usuario.getAtivo());
            } else {
                response.put("usuario_encontrado", false);
                response.put("erro", "Usuário admin@bancobr.com não encontrado");
            }
            
        } catch (Exception e) {
            response.put("erro", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para corrigir a senha do admin
     */
    @GetMapping("/corrigir-senha-admin")
    public ResponseEntity<Map<String, Object>> corrigirSenhaAdmin() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Busca o usuário admin
            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndAtivoTrue("admin@bancobr.com");
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                
                // Gera novo hash para admin123
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String novoHash = encoder.encode("admin123");
                
                // Atualiza a senha no banco
                usuario.setSenhaHash(novoHash);
                usuarioRepository.save(usuario);
                
                // Testa se funcionou
                boolean senhaValida = encoder.matches("admin123", novoHash);
                
                response.put("sucesso", true);
                response.put("mensagem", "Senha do admin corrigida com sucesso!");
                response.put("email", usuario.getEmail());
                response.put("novo_hash", novoHash);
                response.put("senha_valida", senhaValida);
                
            } else {
                response.put("sucesso", false);
                response.put("erro", "Usuário admin@bancobr.com não encontrado");
            }
            
        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("erro", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
} 