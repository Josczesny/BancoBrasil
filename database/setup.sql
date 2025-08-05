-- =====================================================
-- SETUP.SQL - SCRIPT DE INICIALIZAÇÃO COMPLETA
-- =====================================================

-- Verifica se o banco existe, se não, cria
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'banking_system') THEN
        CREATE DATABASE banking_system;
    END IF;
END
$$;

-- Conecta ao banco banking_system
\c banking_system;

-- Executa o schema
\i schema.sql

-- Executa as extensões e triggers
\i init.sql

-- Executa os dados de teste
\i seed.sql

-- Verificação final
SELECT '=== VERIFICAÇÃO FINAL ===' as status;
SELECT 'Usuários:', COUNT(*) FROM usuarios;
SELECT 'Contas:', COUNT(*) FROM contas;
SELECT 'Transações:', COUNT(*) FROM transacoes;
SELECT 'Logs:', COUNT(*) FROM logs;

-- Mostra alguns dados de exemplo
SELECT '=== DADOS DE EXEMPLO ===' as status;
SELECT 'Usuários:' as tabela, nome, email, tipo FROM usuarios LIMIT 3;
SELECT 'Contas:' as tabela, numero_conta, tipo, saldo FROM contas LIMIT 5;
SELECT 'Transações:' as tabela, tipo, valor, descricao FROM transacoes LIMIT 5;

SELECT '=== SETUP CONCLUÍDO COM SUCESSO ===' as status; 