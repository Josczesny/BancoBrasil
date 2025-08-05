-- Script para inserir dados reais no sistema bancário
-- Executar após a criação das tabelas

-- Limpar dados existentes (opcional)
-- DELETE FROM transacoes;
-- DELETE FROM contas;
-- DELETE FROM usuarios;
-- DELETE FROM agencias;

-- Inserir agências
INSERT INTO agencias (id, codigo, nome, endereco, telefone, criada_em) VALUES
('550e8400-e29b-41d4-a716-446655440001', '0001', 'Agência Central', 'Rua das Flores, 123 - Centro', '(11) 3333-4444', NOW()),
('550e8400-e29b-41d4-a716-446655440002', '0002', 'Agência Norte', 'Av. Paulista, 1000 - Bela Vista', '(11) 3333-5555', NOW()),
('550e8400-e29b-41d4-a716-446655440003', '0003', 'Agência Sul', 'Rua Augusta, 500 - Consolação', '(11) 3333-6666', NOW()),
('550e8400-e29b-41d4-a716-446655440004', '0004', 'Agência Leste', 'Av. Brigadeiro Faria Lima, 2000 - Itaim', '(11) 3333-7777', NOW()),
('550e8400-e29b-41d4-a716-446655440005', '0005', 'Agência Oeste', 'Rua Oscar Freire, 300 - Jardins', '(11) 3333-8888', NOW());

-- Inserir usuários (senha: 123456 para todos)
INSERT INTO usuarios (id, nome, email, senha, cpf, telefone, data_nascimento, tipo, ativo, criado_em) VALUES
-- Administradores
('550e8400-e29b-41d4-a716-446655440101', 'João Silva', 'joao.silva@bancobr.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '12345678901', '(11) 99999-1111', '1985-03-15', 'ADMIN', true, NOW()),
('550e8400-e29b-41d4-a716-446655440102', 'Maria Santos', 'maria.santos@bancobr.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '12345678902', '(11) 99999-2222', '1988-07-22', 'ADMIN', true, NOW()),

-- Clientes
('550e8400-e29b-41d4-a716-446655440201', 'Pedro Oliveira', 'pedro.oliveira@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '12345678903', '(11) 99999-3333', '1990-11-08', 'CLIENTE', true, NOW()),
('550e8400-e29b-41d4-a716-446655440202', 'Ana Costa', 'ana.costa@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '12345678904', '(11) 99999-4444', '1992-05-14', 'CLIENTE', true, NOW()),
('550e8400-e29b-41d4-a716-446655440203', 'Carlos Ferreira', 'carlos.ferreira@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '12345678905', '(11) 99999-5555', '1987-09-30', 'CLIENTE', true, NOW()),
('550e8400-e29b-41d4-a716-446655440204', 'Lucia Pereira', 'lucia.pereira@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '12345678906', '(11) 99999-6666', '1995-12-03', 'CLIENTE', true, NOW()),
('550e8400-e29b-41d4-a716-446655440205', 'Roberto Almeida', 'roberto.almeida@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '12345678907', '(11) 99999-7777', '1983-04-18', 'CLIENTE', true, NOW()),
('550e8400-e29b-41d4-a716-446655440206', 'Fernanda Lima', 'fernanda.lima@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '12345678908', '(11) 99999-8888', '1991-08-25', 'CLIENTE', true, NOW()),
('550e8400-e29b-41d4-a716-446655440207', 'Marcos Souza', 'marcos.souza@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '12345678909', '(11) 99999-9999', '1989-01-12', 'CLIENTE', true, NOW()),
('550e8400-e29b-41d4-a716-446655440208', 'Juliana Rocha', 'juliana.rocha@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '12345678910', '(11) 99999-0000', '1993-06-20', 'CLIENTE', true, NOW());

-- Inserir contas
INSERT INTO contas (id, numero_conta, agencia_id, usuario_id, tipo_conta, saldo, limite_credito, ativa, criada_em) VALUES
-- Contas do Pedro Oliveira
('550e8400-e29b-41d4-a716-446655440301', '123456-7', '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440201', 'CORRENTE', 15000.00, 5000.00, true, NOW()),
('550e8400-e29b-41d4-a716-446655440302', '123456-8', '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440201', 'POUPANCA', 25000.00, 0.00, true, NOW()),

-- Contas da Ana Costa
('550e8400-e29b-41d4-a716-446655440303', '234567-8', '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440202', 'CORRENTE', 8500.00, 3000.00, true, NOW()),
('550e8400-e29b-41d4-a716-446655440304', '234567-9', '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440202', 'POUPANCA', 12000.00, 0.00, true, NOW()),

-- Contas do Carlos Ferreira
('550e8400-e29b-41d4-a716-446655440305', '345678-9', '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440203', 'CORRENTE', 22000.00, 8000.00, true, NOW()),

-- Contas da Lucia Pereira
('550e8400-e29b-41d4-a716-446655440306', '456789-0', '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440204', 'CORRENTE', 5000.00, 2000.00, true, NOW()),
('550e8400-e29b-41d4-a716-446655440307', '456789-1', '550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440204', 'POUPANCA', 8000.00, 0.00, true, NOW()),

-- Contas do Roberto Almeida
('550e8400-e29b-41d4-a716-446655440308', '567890-1', '550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440205', 'CORRENTE', 35000.00, 10000.00, true, NOW()),
('550e8400-e29b-41d4-a716-446655440309', '567890-2', '550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440205', 'POUPANCA', 45000.00, 0.00, true, NOW()),

-- Contas da Fernanda Lima
('550e8400-e29b-41d4-a716-446655440310', '678901-2', '550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440206', 'CORRENTE', 12000.00, 4000.00, true, NOW()),

-- Contas do Marcos Souza
('550e8400-e29b-41d4-a716-446655440311', '789012-3', '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440207', 'CORRENTE', 18000.00, 6000.00, true, NOW()),
('550e8400-e29b-41d4-a716-446655440312', '789012-4', '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440207', 'POUPANCA', 30000.00, 0.00, true, NOW()),

-- Contas da Juliana Rocha
('550e8400-e29b-41d4-a716-446655440313', '890123-4', '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440208', 'CORRENTE', 9500.00, 3500.00, true, NOW()),
('550e8400-e29b-41d4-a716-446655440314', '890123-5', '550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440208', 'POUPANCA', 15000.00, 0.00, true, NOW());

-- Inserir transações
INSERT INTO transacoes (id, conta_origem_id, conta_destino_id, tipo_transacao, valor, descricao, status, data_transacao) VALUES
-- Transações do Pedro Oliveira
('550e8400-e29b-41d4-a716-446655440401', '550e8400-e29b-41d4-a716-446655440301', '550e8400-e29b-41d4-a716-446655440302', 'TRANSFERENCIA', 5000.00, 'Transferência para poupança', 'CONCLUIDA', NOW() - INTERVAL '5 days'),
('550e8400-e29b-41d4-a716-446655440402', '550e8400-e29b-41d4-a716-446655440301', NULL, 'DEPOSITO', 3000.00, 'Depósito em dinheiro', 'CONCLUIDA', NOW() - INTERVAL '3 days'),
('550e8400-e29b-41d4-a716-446655440403', '550e8400-e29b-41d4-a716-446655440301', NULL, 'SAQUE', 800.00, 'Saque no caixa eletrônico', 'CONCLUIDA', NOW() - INTERVAL '1 day'),

-- Transações da Ana Costa
('550e8400-e29b-41d4-a716-446655440404', '550e8400-e29b-41d4-a716-446655440303', '550e8400-e29b-41d4-a716-446655440304', 'TRANSFERENCIA', 2000.00, 'Transferência para poupança', 'CONCLUIDA', NOW() - INTERVAL '4 days'),
('550e8400-e29b-41d4-a716-446655440405', '550e8400-e29b-41d4-a716-446655440303', NULL, 'DEPOSITO', 1500.00, 'Depósito via PIX', 'CONCLUIDA', NOW() - INTERVAL '2 days'),

-- Transações do Carlos Ferreira
('550e8400-e29b-41d4-a716-446655440406', '550e8400-e29b-41d4-a716-446655440305', NULL, 'DEPOSITO', 5000.00, 'Depósito de salário', 'CONCLUIDA', NOW() - INTERVAL '6 days'),
('550e8400-e29b-41d4-a716-446655440407', '550e8400-e29b-41d4-a716-446655440305', NULL, 'SAQUE', 1200.00, 'Saque para pagamento', 'CONCLUIDA', NOW() - INTERVAL '1 day'),

-- Transações da Lucia Pereira
('550e8400-e29b-41d4-a716-446655440408', '550e8400-e29b-41d4-a716-446655440306', '550e8400-e29b-41d4-a716-446655440307', 'TRANSFERENCIA', 1000.00, 'Transferência para poupança', 'CONCLUIDA', NOW() - INTERVAL '3 days'),
('550e8400-e29b-41d4-a716-446655440409', '550e8400-e29b-41d4-a716-446655440306', NULL, 'DEPOSITO', 800.00, 'Depósito em dinheiro', 'CONCLUIDA', NOW() - INTERVAL '1 day'),

-- Transações do Roberto Almeida
('550e8400-e29b-41d4-a716-446655440410', '550e8400-e29b-41d4-a716-446655440308', '550e8400-e29b-41d4-a716-446655440309', 'TRANSFERENCIA', 8000.00, 'Transferência para poupança', 'CONCLUIDA', NOW() - INTERVAL '5 days'),
('550e8400-e29b-41d4-a716-446655440411', '550e8400-e29b-41d4-a716-446655440308', NULL, 'SAQUE', 2500.00, 'Saque para viagem', 'CONCLUIDA', NOW() - INTERVAL '2 days'),

-- Transações da Fernanda Lima
('550e8400-e29b-41d4-a716-446655440412', '550e8400-e29b-41d4-a716-446655440310', NULL, 'DEPOSITO', 3000.00, 'Depósito de bônus', 'CONCLUIDA', NOW() - INTERVAL '4 days'),
('550e8400-e29b-41d4-a716-446655440413', '550e8400-e29b-41d4-a716-446655440310', NULL, 'SAQUE', 600.00, 'Saque para compras', 'CONCLUIDA', NOW() - INTERVAL '1 day'),

-- Transações do Marcos Souza
('550e8400-e29b-41d4-a716-446655440414', '550e8400-e29b-41d4-a716-446655440311', '550e8400-e29b-41d4-a716-446655440312', 'TRANSFERENCIA', 4000.00, 'Transferência para poupança', 'CONCLUIDA', NOW() - INTERVAL '6 days'),
('550e8400-e29b-41d4-a716-446655440415', '550e8400-e29b-41d4-a716-446655440311', NULL, 'DEPOSITO', 2500.00, 'Depósito de comissão', 'CONCLUIDA', NOW() - INTERVAL '3 days'),

-- Transações da Juliana Rocha
('550e8400-e29b-41d4-a716-446655440416', '550e8400-e29b-41d4-a716-446655440313', '550e8400-e29b-41d4-a716-446655440314', 'TRANSFERENCIA', 1500.00, 'Transferência para poupança', 'CONCLUIDA', NOW() - INTERVAL '4 days'),
('550e8400-e29b-41d4-a716-446655440417', '550e8400-e29b-41d4-a716-446655440313', NULL, 'DEPOSITO', 1200.00, 'Depósito de freelance', 'CONCLUIDA', NOW() - INTERVAL '2 days'),

-- Transações entre diferentes usuários (transferências)
('550e8400-e29b-41d4-a716-446655440418', '550e8400-e29b-41d4-a716-446655440301', '550e8400-e29b-41d4-a716-446655440303', 'TRANSFERENCIA', 500.00, 'Pagamento de almoço', 'CONCLUIDA', NOW() - INTERVAL '1 day'),
('550e8400-e29b-41d4-a716-446655440419', '550e8400-e29b-41d4-a716-446655440305', '550e8400-e29b-41d4-a716-446655440310', 'TRANSFERENCIA', 800.00, 'Pagamento de conta', 'CONCLUIDA', NOW() - INTERVAL '2 days'),
('550e8400-e29b-41d4-a716-446655440420', '550e8400-e29b-41d4-a716-446655440308', '550e8400-e29b-41d4-a716-446655440311', 'TRANSFERENCIA', 1200.00, 'Pagamento de serviço', 'CONCLUIDA', NOW() - INTERVAL '3 days');

-- Atualizar saldos das contas baseado nas transações
UPDATE contas SET saldo = 15000.00 WHERE id = '550e8400-e29b-41d4-a716-446655440301';
UPDATE contas SET saldo = 25000.00 WHERE id = '550e8400-e29b-41d4-a716-446655440302';
UPDATE contas SET saldo = 8500.00 WHERE id = '550e8400-e29b-41d4-a716-446655440303';
UPDATE contas SET saldo = 12000.00 WHERE id = '550e8400-e29b-41d4-a716-446655440304';
UPDATE contas SET saldo = 22000.00 WHERE id = '550e8400-e29b-41d4-a716-446655440305';
UPDATE contas SET saldo = 5000.00 WHERE id = '550e8400-e29b-41d4-a716-446655440306';
UPDATE contas SET saldo = 8000.00 WHERE id = '550e8400-e29b-41d4-a716-446655440307';
UPDATE contas SET saldo = 35000.00 WHERE id = '550e8400-e29b-41d4-a716-446655440308';
UPDATE contas SET saldo = 45000.00 WHERE id = '550e8400-e29b-41d4-a716-446655440309';
UPDATE contas SET saldo = 12000.00 WHERE id = '550e8400-e29b-41d4-a716-446655440310';
UPDATE contas SET saldo = 18000.00 WHERE id = '550e8400-e29b-41d4-a716-446655440311';
UPDATE contas SET saldo = 30000.00 WHERE id = '550e8400-e29b-41d4-a716-446655440312';
UPDATE contas SET saldo = 9500.00 WHERE id = '550e8400-e29b-41d4-a716-446655440313';
UPDATE contas SET saldo = 15000.00 WHERE id = '550e8400-e29b-41d4-a716-446655440314';

-- Verificar dados inseridos
SELECT 'Agencias inseridas:' as info, COUNT(*) as total FROM agencias
UNION ALL
SELECT 'Usuarios inseridos:', COUNT(*) FROM usuarios
UNION ALL
SELECT 'Contas inseridas:', COUNT(*) FROM contas
UNION ALL
SELECT 'Transacoes inseridas:', COUNT(*) FROM transacoes; 