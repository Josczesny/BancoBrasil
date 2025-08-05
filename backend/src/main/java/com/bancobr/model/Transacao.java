package com.bancobr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade Transacao do Sistema Bancário
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@Entity
@Table(name = "transacoes")
@EntityListeners(AuditingEntityListener.class)
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_origem")
    @JsonIgnore
    private Conta contaOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_destino")
    @JsonIgnore
    private Conta contaDestino;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoTransacao tipo;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @CreatedDate
    @Column(name = "realizada_em", nullable = false, updatable = false)
    private LocalDateTime realizadaEm;

    // Construtores
    public Transacao() {}

    public Transacao(Conta contaOrigem, Conta contaDestino, TipoTransacao tipo, BigDecimal valor, String descricao) {
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Conta getContaOrigem() {
        return contaOrigem;
    }

    public void setContaOrigem(Conta contaOrigem) {
        this.contaOrigem = contaOrigem;
    }

    public Conta getContaDestino() {
        return contaDestino;
    }

    public void setContaDestino(Conta contaDestino) {
        this.contaDestino = contaDestino;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getRealizadaEm() {
        return realizadaEm;
    }

    public void setRealizadaEm(LocalDateTime realizadaEm) {
        this.realizadaEm = realizadaEm;
    }

    /**
     * Processa a transação
     */
    public void processar() {
        if (tipo == TipoTransacao.TRANSFERENCIA) {
            if (contaOrigem != null && contaDestino != null) {
                contaOrigem.debitar(valor);
                contaDestino.creditar(valor);
            }
        } else if (tipo == TipoTransacao.DEPOSITO) {
            if (contaDestino != null) {
                contaDestino.creditar(valor);
            }
        } else if (tipo == TipoTransacao.SAQUE) {
            if (contaOrigem != null) {
                contaOrigem.debitar(valor);
            }
        }
    }

    /**
     * Verifica se é transferência
     */
    public boolean isTransferencia() {
        return tipo == TipoTransacao.TRANSFERENCIA;
    }

    /**
     * Verifica se é depósito
     */
    public boolean isDeposito() {
        return tipo == TipoTransacao.DEPOSITO;
    }

    /**
     * Verifica se é saque
     */
    public boolean isSaque() {
        return tipo == TipoTransacao.SAQUE;
    }

    @Override
    public String toString() {
        return "Transacao{" +
                "id=" + id +
                ", contaOrigem=" + (contaOrigem != null ? contaOrigem.getId() : null) +
                ", contaDestino=" + (contaDestino != null ? contaDestino.getId() : null) +
                ", tipo=" + tipo +
                ", valor=" + valor +
                ", descricao='" + descricao + '\'' +
                ", realizadaEm=" + realizadaEm +
                '}';
    }

    /**
     * Enum para tipos de transação
     */
    public enum TipoTransacao {
        DEPOSITO,
        SAQUE,
        TRANSFERENCIA
    }


} 