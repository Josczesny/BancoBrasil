package com.bancobr.security;

import com.bancobr.model.Usuario;
import com.bancobr.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Serviço customizado para UserDetails
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        // Verifica se a senha hash não é null
        if (usuario.getSenhaHash() == null || usuario.getSenhaHash().isEmpty()) {
            throw new UsernameNotFoundException("Senha não configurada para o usuário: " + email);
        }

        return new User(
                usuario.getEmail(),
                usuario.getSenhaHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getTipo().name()))
        );
    }
} 