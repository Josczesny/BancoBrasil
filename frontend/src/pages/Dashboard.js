import React, { useState, useEffect } from 'react';
import SaldoDisplay from '../components/SaldoDisplay';

const Dashboard = ({ user }) => {
  const [dashboardData, setDashboardData] = useState({
    saldoTotal: 0,
    contasAtivas: 0,
    transacoesHoje: 0,
    ultimasTransacoes: []
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('/api/contas/dashboard', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        const data = await response.json();
        setDashboardData(data);
      } else {
        setError('Erro ao carregar dados do dashboard');
      }
    } catch (error) {
      setError('Erro de conexão');
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '40px' }}>
        <div className="spinner"></div>
        <p>Carregando dashboard...</p>
      </div>
    );
  }

  return (
    <div className="fade-in">
      <div style={{ marginBottom: '32px' }}>
        <h1 style={{ marginBottom: '8px', color: 'var(--text-primary)' }}>
          Dashboard
        </h1>
        <p style={{ color: 'var(--text-secondary)', margin: 0 }}>
          Bem-vindo de volta! {user?.nome}! Aqui está um resumo da sua conta.
        </p>
      </div>

      {error && (
        <div className="alert alert-error">
          {error}
        </div>
      )}

      <SaldoDisplay user={user} />

      <div className="dashboard-grid">
        <div className="dashboard-card">
          <h3>💰 Saldo Total</h3>
          <div className="value">
            {formatCurrency(dashboardData.saldoTotal)}
          </div>
          <div className="label">
            Todas as suas contas
          </div>
        </div>

        <div className="dashboard-card">
          <h3>🏦 Contas Ativas</h3>
          <div className="value">
            {dashboardData.contasAtivas}
          </div>
          <div className="label">
            Contas disponíveis
          </div>
        </div>

        <div className="dashboard-card">
          <h3>📊 Transações Hoje</h3>
          <div className="value">
            {dashboardData.transacoesHoje}
          </div>
          <div className="label">
            Movimentações do dia
          </div>
        </div>

        <div className="dashboard-card">
          <h3>🔒 Segurança</h3>
          <div className="value" style={{ fontSize: '24px' }}>
            Ativo
          </div>
          <div className="label">
            Sistema protegido
          </div>
        </div>
      </div>

      <div className="card">
        <h3 style={{ marginBottom: '24px', color: 'var(--text-primary)' }}>
          Últimas Transações
        </h3>
        
        {dashboardData.ultimasTransacoes.length > 0 ? (
          <div className="table-container">
            <table className="table">
              <thead>
                <tr>
                  <th>Data</th>
                  <th>Tipo</th>
                  <th>Valor</th>
                  <th>Status</th>
                  <th>Descrição</th>
                </tr>
              </thead>
              <tbody>
                {dashboardData.ultimasTransacoes.map((transacao) => (
                  <tr key={transacao.id}>
                    <td>{formatDate(transacao.dataTransacao)}</td>
                    <td>
                      <span style={{ 
                        padding: '4px 8px', 
                        borderRadius: '4px', 
                        fontSize: '12px',
                        background: transacao.tipo === 'TRANSFERENCIA' ? '#dbeafe' : 
                                  transacao.tipo === 'DEPOSITO' ? '#d1fae5' : 
                                  transacao.tipo === 'SAQUE' ? '#fef3c7' : '#f3e8ff',
                        color: transacao.tipo === 'TRANSFERENCIA' ? '#1e40af' : 
                               transacao.tipo === 'DEPOSITO' ? '#059669' : 
                               transacao.tipo === 'SAQUE' ? '#d97706' : '#7c3aed'
                      }}>
                        {transacao.tipo}
                      </span>
                    </td>
                    <td style={{ fontWeight: '600' }}>
                      {formatCurrency(transacao.valor)}
                    </td>
                    <td>
                      <span className={`status status-${transacao.status.toLowerCase()}`}>
                        {transacao.status}
                      </span>
                    </td>
                    <td>{transacao.descricao}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text-secondary)' }}>
            <p>Nenhuma transação encontrada</p>
          </div>
        )}
      </div>

      <div className="card">
        <h3 style={{ marginBottom: '24px', color: 'var(--text-primary)' }}>
          Ações Rápidas
        </h3>
        
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px' }}>
          <button 
            className="btn btn-primary" 
            style={{ width: '100%' }}
            onClick={() => window.location.href = '/transacoes'}
          >
            💸 Nova Transferência
          </button>
          <button 
            className="btn btn-secondary" 
            style={{ width: '100%' }}
            onClick={() => alert('Funcionalidade PIX em desenvolvimento')}
          >
            📱 PIX
          </button>
          <button 
            className="btn btn-secondary" 
            style={{ width: '100%' }}
            onClick={() => window.location.href = '/transacoes'}
          >
            📄 Extrato
          </button>
          <button 
            className="btn btn-secondary" 
            style={{ width: '100%' }}
            onClick={() => alert('Configurações em desenvolvimento')}
          >
            ⚙️ Configurações
          </button>
        </div>
      </div>
    </div>
  );
};

export default Dashboard; 