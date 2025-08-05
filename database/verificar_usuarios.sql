-- =====================================================
-- VERIFICAR_USUARIOS.SQL - VERIFICAÇÃO DOS USUÁRIOS
-- =====================================================

-- Verificar usuários existentes
SELECT 
    id,
    nome,
    email,
    cpf,
    tipo,
    ativo,
    senha_hash,
    criado_em
FROM usuarios 
ORDER BY criado_em;

-- Verificar se o admin existe
SELECT 
    'Admin existe?' as pergunta,
    CASE 
        WHEN COUNT(*) > 0 THEN 'SIM'
        ELSE 'NÃO'
    END as resposta
FROM usuarios 
WHERE email = 'admin@bancobr.com';

-- Verificar formato da senha do admin
SELECT 
    email,
    senha_hash,
    LENGTH(senha_hash) as tamanho_senha,
    LEFT(senha_hash, 7) as inicio_senha
FROM usuarios 
WHERE email = 'admin@bancobr.com'; 