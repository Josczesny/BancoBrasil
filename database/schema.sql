-- =====================================================
-- SCHEMA DO SISTEMA BANCÁRIO - BANCO DO BRASIL
-- =====================================================

-- Extensões necessárias
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Tipos enumerados
CREATE TYPE tipo_usuario AS ENUM ('ADMIN', 'CLIENTE');
CREATE TYPE tipo_conta AS ENUM ('CORRENTE', 'POUPANCA');

-- Tabela de usuários
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    senha_hash TEXT NOT NULL,
    cpf CHAR(11) UNIQUE NOT NULL,
    tipo tipo_usuario NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de contas
CREATE TABLE contas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    agencia VARCHAR(10) NOT NULL,
    numero_conta VARCHAR(20) UNIQUE NOT NULL,
    tipo tipo_conta NOT NULL,
    saldo NUMERIC(15,2) DEFAULT 0,
    limite_credito NUMERIC(15,2) DEFAULT 0,
    criada_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de transações
CREATE TABLE transacoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conta_origem UUID REFERENCES contas(id),
    conta_destino UUID REFERENCES contas(id),
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('DEPOSITO', 'SAQUE', 'TRANSFERENCIA')),
    valor NUMERIC(15,2) NOT NULL,
    realizada_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    descricao TEXT
);

-- Tabela de logs de auditoria
CREATE TABLE logs (
    id SERIAL PRIMARY KEY,
    usuario_id UUID,
    acao TEXT NOT NULL,
    tabela_afetada TEXT,
    registro_id UUID,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para performance
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_cpf ON usuarios(cpf);
CREATE INDEX idx_usuarios_tipo ON usuarios(tipo);
CREATE INDEX idx_usuarios_ativo ON usuarios(ativo);

CREATE INDEX idx_contas_usuario_id ON contas(usuario_id);
CREATE INDEX idx_contas_numero_conta ON contas(numero_conta);
CREATE INDEX idx_contas_agencia ON contas(agencia);

CREATE INDEX idx_transacoes_conta_origem ON transacoes(conta_origem);
CREATE INDEX idx_transacoes_conta_destino ON transacoes(conta_destino);
CREATE INDEX idx_transacoes_tipo ON transacoes(tipo);
CREATE INDEX idx_transacoes_realizada_em ON transacoes(realizada_em);

CREATE INDEX idx_logs_usuario_id ON logs(usuario_id);
CREATE INDEX idx_logs_timestamp ON logs(timestamp);
CREATE INDEX idx_logs_tabela_afetada ON logs(tabela_afetada);

-- Constraints adicionais
ALTER TABLE contas ADD CONSTRAINT chk_saldo_positivo CHECK (saldo >= 0);
ALTER TABLE contas ADD CONSTRAINT chk_limite_credito_positivo CHECK (limite_credito >= 0);
ALTER TABLE transacoes ADD CONSTRAINT chk_valor_positivo CHECK (valor > 0);
ALTER TABLE transacoes ADD CONSTRAINT chk_conta_origem_destino_diferentes CHECK (conta_origem != conta_destino);

-- Comentários para documentação
COMMENT ON TABLE usuarios IS 'Tabela de usuários do sistema bancário';
COMMENT ON TABLE contas IS 'Tabela de contas bancárias';
COMMENT ON TABLE transacoes IS 'Tabela de transações financeiras';
COMMENT ON TABLE logs IS 'Tabela de logs de auditoria'; 