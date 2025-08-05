package com.bancobr.service;

import com.bancobr.model.Usuario;
import com.bancobr.repository.UsuarioRepository;
import com.bancobr.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Serviço para gerenciamento de usuários
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LogService logService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Cria um novo usuário
     */
    public Usuario criarUsuario(Usuario usuario) {
        // Validações
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }
        if (usuarioRepository.existsByCpf(usuario.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        // Criptografa a senha
        usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
        usuario.setAtivo(true);
        usuario.setCriadoEm(LocalDateTime.now());

        // Salva o usuário
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Registra log (comentado temporariamente para resolver problema jsonb)
        // logService.criarLog(usuarioSalvo, "INSERT", "usuarios", usuarioSalvo.getId());

        return usuarioSalvo;
    }

    /**
     * Busca usuário por ID
     */
    public Optional<Usuario> buscarPorId(UUID id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Busca usuário por email
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmailAndAtivoTrue(email);
    }

    /**
     * Busca usuário por CPF
     */
    public Optional<Usuario> buscarPorCpf(String cpf) {
        return usuarioRepository.findByCpf(cpf);
    }

    /**
     * Lista todos os usuários
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Lista usuários ativos
     */
    public List<Usuario> listarAtivos() {
        return usuarioRepository.findByAtivoTrue();
    }

    /**
     * Lista usuários por tipo
     */
    public List<Usuario> listarPorTipo(Usuario.TipoUsuario tipo) {
        return usuarioRepository.findByTipoAndAtivoTrue(tipo);
    }

    /**
     * Busca usuários por nome
     */
    public List<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Atualiza dados do usuário
     */
    public Usuario atualizarUsuario(UUID id, Usuario usuarioAtualizado) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Atualiza campos permitidos
        if (usuarioAtualizado.getNome() != null) {
            usuario.setNome(usuarioAtualizado.getNome());
        }
        if (usuarioAtualizado.getEmail() != null && !usuarioAtualizado.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())) {
                throw new RuntimeException("Email já cadastrado");
            }
            usuario.setEmail(usuarioAtualizado.getEmail());
        }
        if (usuarioAtualizado.getCpf() != null && !usuarioAtualizado.getCpf().equals(usuario.getCpf())) {
            if (usuarioRepository.existsByCpf(usuarioAtualizado.getCpf())) {
                throw new RuntimeException("CPF já cadastrado");
            }
            usuario.setCpf(usuarioAtualizado.getCpf());
        }
        if (usuarioAtualizado.getTipo() != null) {
            usuario.setTipo(usuarioAtualizado.getTipo());
        }

        usuario.setAtualizadoEm(LocalDateTime.now());
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Registra log
        logService.criarLog(usuarioSalvo, "UPDATE", "usuarios", usuarioSalvo.getId());

        return usuarioSalvo;
    }

    /**
     * Atualiza senha do usuário
     */
    public void atualizarSenha(UUID id, String novaSenha) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuario.setAtualizadoEm(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // Registra log
        logService.criarLog(usuario, "UPDATE", "usuarios", usuario.getId());
    }

    /**
     * Ativa/desativa usuário
     */
    public Usuario alterarStatus(UUID id, boolean ativo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setAtivo(ativo);
        usuario.setAtualizadoEm(LocalDateTime.now());
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Registra log
        logService.criarLog(usuarioSalvo, "UPDATE", "usuarios", usuarioSalvo.getId());

        return usuarioSalvo;
    }

    /**
     * Remove usuário (soft delete)
     */
    public void removerUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setAtivo(false);
        usuario.setAtualizadoEm(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // Registra log
        logService.criarLog(usuario, "DELETE", "usuarios", usuario.getId());
    }

    /**
     * Valida credenciais do usuário
     */
    public Optional<Usuario> validarCredenciais(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmailAndAtivoTrue(email);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (passwordEncoder.matches(senha, usuario.getSenhaHash())) {
                return Optional.of(usuario);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Verifica se email existe
     */
    public boolean emailExiste(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    /**
     * Verifica se CPF existe
     */
    public boolean cpfExiste(String cpf) {
        return usuarioRepository.existsByCpf(cpf);
    }

    /**
     * Conta usuários por tipo
     */
    public long contarPorTipo(Usuario.TipoUsuario tipo) {
        return usuarioRepository.countByTipo(tipo);
    }

    /**
     * Conta usuários ativos
     */
    public long contarAtivos() {
        return usuarioRepository.countByAtivoTrue();
    }

    /**
     * Busca usuário com contas
     */
    public Optional<Usuario> buscarComContas(UUID id) {
        return usuarioRepository.findByIdWithContas(id);
    }

    /**
     * Busca usuário com logs
     */
    public Optional<Usuario> buscarComLogs(UUID id) {
        return usuarioRepository.findByIdWithLogs(id);
    }

    /**
     * Lista usuários criados após uma data
     */
    public List<Usuario> listarCriadosApos(LocalDateTime data) {
        return usuarioRepository.findByCriadoEmAfter(data);
    }

    /**
     * Lista usuários criados entre duas datas
     */
    public List<Usuario> listarCriadosEntre(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return usuarioRepository.findByCriadoEmBetween(dataInicio, dataFim);
    }
} 