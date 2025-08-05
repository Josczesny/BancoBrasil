-- Script de inicialização do banco de dados
-- Sistema Bancário - Banco do Brasil
-- PostgreSQL

-- Criar banco de dados se não existir
-- \i schema.sql
-- \i seed.sql

-- Verificar se o banco existe
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'banking_system') THEN
        CREATE DATABASE banking_system;
    END IF;
END
$$;

-- Conectar ao banco
\c banking_system;

-- Executar schema
\i schema.sql

-- Executar seed
\i seed.sql

-- Verificar se tudo foi criado corretamente
SELECT 'Schema criado com sucesso!' as status;

-- Verificar tabelas criadas
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'banking' 
ORDER BY table_name;

-- Verificar dados inseridos
SELECT 'Usuários:' as tipo, COUNT(*) as total FROM banking.usuarios
UNION ALL
SELECT 'Agências:', COUNT(*) FROM banking.agencias
UNION ALL
SELECT 'Contas:', COUNT(*) FROM banking.contas
UNION ALL
SELECT 'Transações:', COUNT(*) FROM banking.transacoes
UNION ALL
SELECT 'Configurações:', COUNT(*) FROM banking.configuracoes; 

-- =====================================================
-- INIT.SQL - EXTENSÕES E TRIGGERS DO SISTEMA BANCÁRIO
-- =====================================================

-- Extensões adicionais
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Função para log de inserção
CREATE OR REPLACE FUNCTION log_insercao()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO logs(usuario_id, acao, tabela_afetada, registro_id)
    VALUES (NEW.usuario_id, 'INSERT', TG_TABLE_NAME, NEW.id);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Função para log de atualização
CREATE OR REPLACE FUNCTION log_atualizacao()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO logs(usuario_id, acao, tabela_afetada, registro_id)
    VALUES (COALESCE(NEW.usuario_id, OLD.usuario_id), 'UPDATE', TG_TABLE_NAME, NEW.id);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Função para log de exclusão
CREATE OR REPLACE FUNCTION log_exclusao()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO logs(usuario_id, acao, tabela_afetada, registro_id)
    VALUES (OLD.usuario_id, 'DELETE', TG_TABLE_NAME, OLD.id);
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Triggers para tabela usuarios
CREATE TRIGGER trg_log_usuario_insert
AFTER INSERT ON usuarios
FOR EACH ROW
EXECUTE FUNCTION log_insercao();

CREATE TRIGGER trg_log_usuario_update
AFTER UPDATE ON usuarios
FOR EACH ROW
EXECUTE FUNCTION log_atualizacao();

CREATE TRIGGER trg_log_usuario_delete
AFTER DELETE ON usuarios
FOR EACH ROW
EXECUTE FUNCTION log_exclusao();

-- Triggers para tabela contas
CREATE TRIGGER trg_log_conta_insert
AFTER INSERT ON contas
FOR EACH ROW
EXECUTE FUNCTION log_insercao();

CREATE TRIGGER trg_log_conta_update
AFTER UPDATE ON contas
FOR EACH ROW
EXECUTE FUNCTION log_atualizacao();

CREATE TRIGGER trg_log_conta_delete
AFTER DELETE ON contas
FOR EACH ROW
EXECUTE FUNCTION log_exclusao();

-- Triggers para tabela transacoes
CREATE TRIGGER trg_log_transacao_insert
AFTER INSERT ON transacoes
FOR EACH ROW
EXECUTE FUNCTION log_insercao();

CREATE TRIGGER trg_log_transacao_update
AFTER UPDATE ON transacoes
FOR EACH ROW
EXECUTE FUNCTION log_atualizacao();

CREATE TRIGGER trg_log_transacao_delete
AFTER DELETE ON transacoes
FOR EACH ROW
EXECUTE FUNCTION log_exclusao();

-- Função para validar CPF
CREATE OR REPLACE FUNCTION validar_cpf(cpf CHAR(11))
RETURNS BOOLEAN AS $$
DECLARE
    soma1 INTEGER := 0;
    soma2 INTEGER := 0;
    i INTEGER;
    digito1 INTEGER;
    digito2 INTEGER;
BEGIN
    -- Verifica se todos os dígitos são iguais
    IF cpf = '00000000000' OR cpf = '11111111111' OR cpf = '22222222222' OR
       cpf = '33333333333' OR cpf = '44444444444' OR cpf = '55555555555' OR
       cpf = '66666666666' OR cpf = '77777777777' OR cpf = '88888888888' OR
       cpf = '99999999999' THEN
        RETURN FALSE;
    END IF;
    
    -- Calcula primeiro dígito verificador
    FOR i IN 1..9 LOOP
        soma1 := soma1 + (SUBSTRING(cpf FROM i FOR 1)::INTEGER * (11 - i));
    END LOOP;
    
    digito1 := 11 - (soma1 % 11);
    IF digito1 > 9 THEN
        digito1 := 0;
    END IF;
    
    -- Calcula segundo dígito verificador
    FOR i IN 1..10 LOOP
        soma2 := soma2 + (SUBSTRING(cpf FROM i FOR 1)::INTEGER * (12 - i));
    END LOOP;
    
    digito2 := 11 - (soma2 % 11);
    IF digito2 > 9 THEN
        digito2 := 0;
    END IF;
    
    -- Verifica se os dígitos calculados são iguais aos do CPF
    RETURN (SUBSTRING(cpf FROM 10 FOR 1)::INTEGER = digito1) AND
           (SUBSTRING(cpf FROM 11 FOR 1)::INTEGER = digito2);
END;
$$ LANGUAGE plpgsql;

-- Constraint para validar CPF
ALTER TABLE usuarios ADD CONSTRAINT chk_cpf_valido 
CHECK (validar_cpf(cpf));

-- Função para gerar número de conta único
CREATE OR REPLACE FUNCTION gerar_numero_conta()
RETURNS VARCHAR(20) AS $$
DECLARE
    numero_conta VARCHAR(20);
    existe BOOLEAN;
BEGIN
    LOOP
        -- Gera número no formato: 000000-0
        numero_conta := LPAD(FLOOR(RANDOM() * 999999)::TEXT, 6, '0') || '-' || 
                       FLOOR(RANDOM() * 9 + 1)::TEXT;
        
        -- Verifica se já existe
        SELECT EXISTS(SELECT 1 FROM contas WHERE numero_conta = numero_conta) INTO existe;
        
        IF NOT existe THEN
            RETURN numero_conta;
        END IF;
    END LOOP;
END;
$$ LANGUAGE plpgsql;

-- Função para atualizar saldo após transação
CREATE OR REPLACE FUNCTION atualizar_saldo()
RETURNS TRIGGER AS $$
BEGIN
    -- Atualiza saldo da conta origem (se existir)
    IF NEW.conta_origem IS NOT NULL THEN
        UPDATE contas 
        SET saldo = saldo - NEW.valor 
        WHERE id = NEW.conta_origem;
    END IF;
    
    -- Atualiza saldo da conta destino (se existir)
    IF NEW.conta_destino IS NOT NULL THEN
        UPDATE contas 
        SET saldo = saldo + NEW.valor 
        WHERE id = NEW.conta_destino;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para atualizar saldo automaticamente
CREATE TRIGGER trg_atualizar_saldo
AFTER INSERT ON transacoes
FOR EACH ROW
EXECUTE FUNCTION atualizar_saldo(); 