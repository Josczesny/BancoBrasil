package com.bancobr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade Log do Sistema Bancário
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@Entity
@Table(name = "logs")
@EntityListeners(AuditingEntityListener.class)
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @NotBlank(message = "Ação é obrigatória")
    @Column(name = "acao", nullable = false)
    private String acao;

    @Column(name = "tabela", nullable = false)
    private String tabela;

    @Column(name = "registro_id")
    private UUID registroId;

    @Column(name = "dados_anteriores", columnDefinition = "jsonb")
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.OTHER)
    private String dadosAnteriores;

    @Column(name = "dados_novos", columnDefinition = "jsonb")
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.OTHER)
    private String dadosNovos;

    @CreatedDate
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    // Construtores
    public Log() {}

    public Log(Usuario usuario, String acao, String tabela, UUID registroId) {
        this.usuario = usuario;
        this.acao = acao;
        this.tabela = tabela;
        this.registroId = registroId;
        this.dadosAnteriores = null;
        this.dadosNovos = null;
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

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public String getDadosAnteriores() {
        return dadosAnteriores;
    }

    public void setDadosAnteriores(String dadosAnteriores) {
        this.dadosAnteriores = dadosAnteriores;
    }

    public String getDadosNovos() {
        return dadosNovos;
    }

    public void setDadosNovos(String dadosNovos) {
        this.dadosNovos = dadosNovos;
    }

    public UUID getRegistroId() {
        return registroId;
    }

    public void setRegistroId(UUID registroId) {
        this.registroId = registroId;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", acao='" + acao + '\'' +
                ", tabela='" + tabela + '\'' +
                ", registroId=" + registroId +
                ", dadosAnteriores='" + dadosAnteriores + '\'' +
                ", dadosNovos='" + dadosNovos + '\'' +
                ", criadoEm=" + criadoEm +
                '}';
    }
} 