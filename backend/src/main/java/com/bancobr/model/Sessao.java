package com.bancobr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade Sessao do Sistema Bancário
 */
@Entity
@Table(name = "sessoes")
@EntityListeners(AuditingEntityListener.class)
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull(message = "Usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @NotBlank(message = "Token é obrigatório")
    @Column(name = "token", nullable = false, length = 500)
    private String token;

    @NotNull(message = "Data de expiração é obrigatória")
    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @Column(name = "ativa", nullable = false)
    private Boolean ativa = true;

    @CreatedDate
    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    // Construtores
    public Sessao() {}

    public Sessao(Usuario usuario, String token, LocalDateTime expiraEm) {
        this.usuario = usuario;
        this.token = token;
        this.expiraEm = expiraEm;
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiraEm() {
        return expiraEm;
    }

    public void setExpiraEm(LocalDateTime expiraEm) {
        this.expiraEm = expiraEm;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public void setCriadaEm(LocalDateTime criadaEm) {
        this.criadaEm = criadaEm;
    }

    /**
     * Verifica se a sessão está expirada
     */
    public boolean isExpirada() {
        return LocalDateTime.now().isAfter(expiraEm);
    }

    /**
     * Invalida a sessão
     */
    public void invalidar() {
        this.ativa = false;
    }

    @Override
    public String toString() {
        return "Sessao{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", expiraEm=" + expiraEm +
                ", ativa=" + ativa +
                '}';
    }
} 