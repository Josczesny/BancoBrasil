-- =====================================================
-- SEED.SQL - DADOS DE TESTE DO SISTEMA BANCÁRIO
-- =====================================================

-- Inserção de usuários fictícios
-- Senhas em BCrypt (compatível com Spring Security)
INSERT INTO usuarios (nome, email, senha_hash, cpf, tipo) VALUES
('Administrador Sistema', 'admin@bancobr.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '00000000000', 'ADMIN'),
('João Silva Santos', 'joao.silva@email.com', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfgqwAG6a3Tp5rYVJ9qK3qK3qK3qK3q', '11111111111', 'CLIENTE'),
('Maria Oliveira Costa', 'maria.oliveira@email.com', '$2a$10$9L2q/b1eM2MYNjhFEHGsxPgqrxBG7b4Uq6rZWI0rL4rL4rL4rL4r', '22222222222', 'CLIENTE'),
('Pedro Santos Lima', 'pedro.santos@email.com', '$2a$10$0M3r/c2fN3OZkIGFIHtHyQrqsyCH8c5Vr7sAXJ1sM5sM5sM5sM5s', '33333333333', 'CLIENTE'),
('Ana Paula Ferreira', 'ana.ferreira@email.com', '$2a$10$1N4s/d3gO4PAljHGJIJuIzSrtzDI9d6Ws8tBYJ2tN6tN6tN6tN6t', '44444444444', 'CLIENTE');

-- Inserção de contas bancárias
INSERT INTO contas (usuario_id, agencia, numero_conta, tipo, saldo, limite_credito) VALUES
-- Contas do Administrador
((SELECT id FROM usuarios WHERE email = 'admin@bancobr.com'), '0001', '123456-7', 'CORRENTE', 50000.00, 10000.00),
((SELECT id FROM usuarios WHERE email = 'admin@bancobr.com'), '0001', '123456-8', 'POUPANCA', 25000.00, 0.00),

-- Contas do João Silva
((SELECT id FROM usuarios WHERE email = 'joao.silva@email.com'), '0001', '765432-1', 'CORRENTE', 15000.00, 3000.00),
((SELECT id FROM usuarios WHERE email = 'joao.silva@email.com'), '0001', '765432-2', 'POUPANCA', 8000.00, 0.00),

-- Contas da Maria Oliveira
((SELECT id FROM usuarios WHERE email = 'maria.oliveira@email.com'), '0002', '987654-3', 'CORRENTE', 22000.00, 5000.00),
((SELECT id FROM usuarios WHERE email = 'maria.oliveira@email.com'), '0002', '987654-4', 'POUPANCA', 12000.00, 0.00),

-- Contas do Pedro Santos
((SELECT id FROM usuarios WHERE email = 'pedro.santos@email.com'), '0003', '456789-5', 'CORRENTE', 18000.00, 4000.00),
((SELECT id FROM usuarios WHERE email = 'pedro.santos@email.com'), '0003', '456789-6', 'POUPANCA', 9500.00, 0.00),

-- Contas da Ana Paula
((SELECT id FROM usuarios WHERE email = 'ana.ferreira@email.com'), '0004', '654321-7', 'CORRENTE', 12000.00, 2500.00),
((SELECT id FROM usuarios WHERE email = 'ana.ferreira@email.com'), '0004', '654321-8', 'POUPANCA', 6000.00, 0.00);

-- Inserção de transações simuladas
INSERT INTO transacoes (conta_origem, conta_destino, tipo, valor, descricao) VALUES
-- Transferências entre contas
((SELECT id FROM contas WHERE numero_conta = '123456-7'), (SELECT id FROM contas WHERE numero_conta = '765432-1'), 'TRANSFERENCIA', 5000.00, 'Transferência inicial para João Silva'),
((SELECT id FROM contas WHERE numero_conta = '765432-1'), (SELECT id FROM contas WHERE numero_conta = '987654-3'), 'TRANSFERENCIA', 2000.00, 'Pagamento de serviços'),
((SELECT id FROM contas WHERE numero_conta = '987654-3'), (SELECT id FROM contas WHERE numero_conta = '456789-5'), 'TRANSFERENCIA', 1500.00, 'Transferência para Pedro Santos'),
((SELECT id FROM contas WHERE numero_conta = '456789-5'), (SELECT id FROM contas WHERE numero_conta = '654321-7'), 'TRANSFERENCIA', 800.00, 'Pagamento de conta'),

-- Depósitos
(NULL, (SELECT id FROM contas WHERE numero_conta = '765432-2'), 'DEPOSITO', 3000.00, 'Depósito em poupança'),
(NULL, (SELECT id FROM contas WHERE numero_conta = '987654-4'), 'DEPOSITO', 2500.00, 'Depósito em poupança'),
(NULL, (SELECT id FROM contas WHERE numero_conta = '456789-6'), 'DEPOSITO', 1800.00, 'Depósito em poupança'),
(NULL, (SELECT id FROM contas WHERE numero_conta = '654321-8'), 'DEPOSITO', 1200.00, 'Depósito em poupança'),

-- Saques
((SELECT id FROM contas WHERE numero_conta = '123456-7'), NULL, 'SAQUE', 1000.00, 'Saque em caixa eletrônico'),
((SELECT id FROM contas WHERE numero_conta = '765432-1'), NULL, 'SAQUE', 500.00, 'Saque em caixa eletrônico'),
((SELECT id FROM contas WHERE numero_conta = '987654-3'), NULL, 'SAQUE', 750.00, 'Saque em caixa eletrônico'),
((SELECT id FROM contas WHERE numero_conta = '456789-5'), NULL, 'SAQUE', 300.00, 'Saque em caixa eletrônico'),

-- Mais transferências
((SELECT id FROM contas WHERE numero_conta = '765432-1'), (SELECT id FROM contas WHERE numero_conta = '654321-7'), 'TRANSFERENCIA', 1200.00, 'Transferência para Ana Paula'),
((SELECT id FROM contas WHERE numero_conta = '987654-3'), (SELECT id FROM contas WHERE numero_conta = '765432-1'), 'TRANSFERENCIA', 800.00, 'Devolução de pagamento'),
((SELECT id FROM contas WHERE numero_conta = '456789-5'), (SELECT id FROM contas WHERE numero_conta = '987654-3'), 'TRANSFERENCIA', 600.00, 'Pagamento de dívida'),
((SELECT id FROM contas WHERE numero_conta = '654321-7'), (SELECT id FROM contas WHERE numero_conta = '456789-5'), 'TRANSFERENCIA', 400.00, 'Transferência para Pedro'),

-- Depósitos adicionais
(NULL, (SELECT id FROM contas WHERE numero_conta = '123456-8'), 'DEPOSITO', 5000.00, 'Depósito em poupança'),
(NULL, (SELECT id FROM contas WHERE numero_conta = '765432-2'), 'DEPOSITO', 2000.00, 'Depósito em poupança'),
(NULL, (SELECT id FROM contas WHERE numero_conta = '987654-4'), 'DEPOSITO', 3000.00, 'Depósito em poupança'),
(NULL, (SELECT id FROM contas WHERE numero_conta = '456789-6'), 'DEPOSITO', 1500.00, 'Depósito em poupança'),
(NULL, (SELECT id FROM contas WHERE numero_conta = '654321-8'), 'DEPOSITO', 1000.00, 'Depósito em poupança');

-- Logs de auditoria simulados
INSERT INTO logs (usuario_id, acao, tabela_afetada, registro_id) VALUES
-- Logs de criação de usuários
((SELECT id FROM usuarios WHERE email = 'admin@bancobr.com'), 'INSERT', 'usuarios', (SELECT id FROM usuarios WHERE email = 'admin@bancobr.com')),
((SELECT id FROM usuarios WHERE email = 'joao.silva@email.com'), 'INSERT', 'usuarios', (SELECT id FROM usuarios WHERE email = 'joao.silva@email.com')),
((SELECT id FROM usuarios WHERE email = 'maria.oliveira@email.com'), 'INSERT', 'usuarios', (SELECT id FROM usuarios WHERE email = 'maria.oliveira@email.com')),
((SELECT id FROM usuarios WHERE email = 'pedro.santos@email.com'), 'INSERT', 'usuarios', (SELECT id FROM usuarios WHERE email = 'pedro.santos@email.com')),
((SELECT id FROM usuarios WHERE email = 'ana.ferreira@email.com'), 'INSERT', 'usuarios', (SELECT id FROM usuarios WHERE email = 'ana.ferreira@email.com')),

-- Logs de criação de contas
((SELECT id FROM usuarios WHERE email = 'admin@bancobr.com'), 'INSERT', 'contas', (SELECT id FROM contas WHERE numero_conta = '123456-7')),
((SELECT id FROM usuarios WHERE email = 'admin@bancobr.com'), 'INSERT', 'contas', (SELECT id FROM contas WHERE numero_conta = '123456-8')),
((SELECT id FROM usuarios WHERE email = 'joao.silva@email.com'), 'INSERT', 'contas', (SELECT id FROM contas WHERE numero_conta = '765432-1')),
((SELECT id FROM usuarios WHERE email = 'joao.silva@email.com'), 'INSERT', 'contas', (SELECT id FROM contas WHERE numero_conta = '765432-2')),
((SELECT id FROM usuarios WHERE email = 'maria.oliveira@email.com'), 'INSERT', 'contas', (SELECT id FROM contas WHERE numero_conta = '987654-3')),
((SELECT id FROM usuarios WHERE email = 'maria.oliveira@email.com'), 'INSERT', 'contas', (SELECT id FROM contas WHERE numero_conta = '987654-4')),
((SELECT id FROM usuarios WHERE email = 'pedro.santos@email.com'), 'INSERT', 'contas', (SELECT id FROM contas WHERE numero_conta = '456789-5')),
((SELECT id FROM usuarios WHERE email = 'pedro.santos@email.com'), 'INSERT', 'contas', (SELECT id FROM contas WHERE numero_conta = '456789-6')),
((SELECT id FROM usuarios WHERE email = 'ana.ferreira@email.com'), 'INSERT', 'contas', (SELECT id FROM contas WHERE numero_conta = '654321-7')),
((SELECT id FROM usuarios WHERE email = 'ana.ferreira@email.com'), 'INSERT', 'contas', (SELECT id FROM contas WHERE numero_conta = '654321-8'));

-- Verificação dos dados inseridos
SELECT 'Usuários criados:' as info, COUNT(*) as total FROM usuarios
UNION ALL
SELECT 'Contas criadas:', COUNT(*) FROM contas
UNION ALL
SELECT 'Transações criadas:', COUNT(*) FROM transacoes
UNION ALL
SELECT 'Logs criados:', COUNT(*) FROM logs; 