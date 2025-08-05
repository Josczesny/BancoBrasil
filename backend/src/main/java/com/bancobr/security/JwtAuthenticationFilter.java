package com.bancobr.security;

import com.bancobr.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT para autenticação
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        System.out.println("🎯 JwtFilter executando para: " + request.getRequestURI());
        
        final String authHeader = request.getHeader("Authorization");
        System.out.println("📦 Authorization Header: " + authHeader);
        
        final String jwt;
        final String userEmail;

        // Verifica se o header Authorization existe e começa com "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ Header Authorization inválido ou ausente");
            filterChain.doFilter(request, response);
            return;
        }

        // Extrai o token JWT
        jwt = authHeader.substring(7);
        System.out.println("🔑 JWT Token: " + jwt.substring(0, Math.min(50, jwt.length())) + "...");
        
        userEmail = jwtService.extractUsername(jwt);
        System.out.println("📧 User Email: " + userEmail);

        // Se o email foi extraído e não há autenticação atual
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("👤 Carregando UserDetails para: " + userEmail);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            
            // Se o token é válido
            if (jwtService.isTokenValid(jwt, userDetails)) {
                System.out.println("✅ Token válido, criando autenticação");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("🔐 Autenticação configurada com sucesso");
            } else {
                System.out.println("❌ Token inválido");
            }
        } else {
            System.out.println("⚠️ Email nulo ou já autenticado");
        }
        
        filterChain.doFilter(request, response);
    }
} 