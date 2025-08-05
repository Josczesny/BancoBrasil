import React, { useState, useEffect } from 'react';

const SaldoDisplay = ({ user }) => {
  const [contas, setContas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchContas();
  }, []);

  const fetchContas = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('/api/contas/usuario', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        const data = await response.json();
        setContas(data);
      } else {
        setError('Erro ao carregar contas');
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

  const getTipoContaIcon = (tipo) => {
    switch (tipo) {
      case 'CORRENTE':
        return '🏦';
      case 'POUPANCA':
        return '💰';
      default:
        return '💳';
    }
  };

  const getTipoContaColor = (tipo) => {
    switch (tipo) {
      case 'CORRENTE':
        return '#3b82f6';
      case 'POUPANCA':
        return '#10b981';
      default:
        return '#6b7280';
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '20px' }}>
        <div className="spinner"></div>
        <p style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>Carregando saldos...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ textAlign: 'center', padding: '20px', color: '#ef4444' }}>
        <p>{error}</p>
      </div>
    );
  }

  const saldoTotal = contas.reduce((total, conta) => total + parseFloat(conta.saldo), 0);

  return (
    <div className="saldo-display">
      <div style={{ 
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        borderRadius: '12px',
        padding: '20px',
        marginBottom: '20px',
        color: 'white',
        boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <h3 style={{ margin: 0, fontSize: '18px', fontWeight: '600' }}>
            💰 Saldo Total
          </h3>
          <span style={{ fontSize: '24px', fontWeight: 'bold' }}>
            {formatCurrency(saldoTotal)}
          </span>
        </div>
        
        <div style={{ fontSize: '14px', opacity: 0.9 }}>
          {contas.length} conta{contas.length !== 1 ? 's' : ''} ativa{contas.length !== 1 ? 's' : ''}
        </div>
      </div>

      <div style={{ display: 'grid', gap: '12px' }}>
        {contas.map((conta) => (
          <div key={conta.id} style={{
            background: 'var(--background-light)',
            borderRadius: '8px',
            padding: '16px',
            border: `2px solid ${getTipoContaColor(conta.tipo)}20`,
            borderLeft: `4px solid ${getTipoContaColor(conta.tipo)}`
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <div style={{ display: 'flex', alignItems: 'center', marginBottom: '4px' }}>
                  <span style={{ marginRight: '8px', fontSize: '16px' }}>
                    {getTipoContaIcon(conta.tipo)}
                  </span>
                  <span style={{ 
                    fontWeight: '600', 
                    color: 'var(--text-primary)',
                    fontSize: '14px'
                  }}>
                    {conta.tipo === 'CORRENTE' ? 'Conta Corrente' : 'Conta Poupança'}
                  </span>
                </div>
                <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>
                  {conta.agencia} • {conta.numeroConta}
                </div>
              </div>
              
              <div style={{ textAlign: 'right' }}>
                <div style={{ 
                  fontSize: '18px', 
                  fontWeight: 'bold',
                  color: 'var(--text-primary)'
                }}>
                  {formatCurrency(conta.saldo)}
                </div>
                {conta.limiteCredito > 0 && (
                  <div style={{ 
                    fontSize: '11px', 
                    color: 'var(--text-secondary)',
                    marginTop: '2px'
                  }}>
                    Limite: {formatCurrency(conta.limiteCredito)}
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default SaldoDisplay; 