import React, { useState, useEffect } from 'react';
import Notification from '../components/Notification';
import SaldoDisplay from '../components/SaldoDisplay';

const Transacoes = ({ user }) => {
  const [transacoes, setTransacoes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [notification, setNotification] = useState(null);
  const [formData, setFormData] = useState({
    contaOrigem: '',
    contaDestino: '',
    valor: '',
    descricao: ''
  });
  const [contas, setContas] = useState([]);

  useEffect(() => {
    fetchTransacoes();
    fetchContas();
  }, []);

  const fetchTransacoes = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('/api/transacoes/usuario', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        const data = await response.json();
        setTransacoes(data);
      } else {
        setError('Erro ao carregar transações');
      }
    } catch (error) {
      setError('Erro de conexão');
    } finally {
      setLoading(false);
    }
  };

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
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    // Validação dos campos obrigatórios
    if (!formData.contaOrigem || !formData.contaDestino || !formData.valor) {
      setNotification({
        message: 'Todos os campos são obrigatórios',
        type: 'warning'
      });
      setLoading(false);
      return;
    }

    if (formData.contaOrigem === formData.contaDestino) {
      setNotification({
        message: 'Conta origem e destino não podem ser iguais',
        type: 'warning'
      });
      setLoading(false);
      return;
    }

    try {
      const token = localStorage.getItem('token');
      console.log('🔍 [DEBUG] Enviando dados:', formData);
      
      const response = await fetch('/api/transacoes/transferencia', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        await response.json();
        setShowForm(false);
        setFormData({ contaOrigem: '', contaDestino: '', valor: '', descricao: '' });
        fetchTransacoes();
        setNotification({
          message: 'Transferência realizada com sucesso!',
          type: 'success'
        });
      } else {
        const errorData = await response.json();
        setNotification({
          message: errorData.error || 'Erro ao realizar transferência',
          type: 'error'
        });
      }
    } catch (error) {
      console.error('❌ [DEBUG] Erro na transferência:', error);
      setNotification({
        message: 'Erro de conexão',
        type: 'error'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
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
        <p>Carregando transações...</p>
      </div>
    );
  }

  return (
    <div className="fade-in">
      {notification && (
        <Notification
          message={notification.message}
          type={notification.type}
          onClose={() => setNotification(null)}
        />
      )}
      <div style={{ marginBottom: '32px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 style={{ marginBottom: '8px', color: 'var(--text-primary)' }}>
            Transações
          </h1>
          <p style={{ color: 'var(--text-secondary)', margin: 0 }}>
            Histórico e novas transações
          </p>
        </div>
        
        <button 
          className="btn btn-primary"
          onClick={() => setShowForm(!showForm)}
        >
          {showForm ? 'Cancelar' : '💸 Nova Transferência'}
        </button>
      </div>

      <SaldoDisplay user={user} />

      {showForm && (
        <div className="card">
          <h3 style={{ marginBottom: '24px', color: 'var(--text-primary)' }}>
            Nova Transferência
          </h3>
          
          <form onSubmit={handleSubmit}>
                         <div className="form-row">
               <div className="form-group">
                 <label htmlFor="contaOrigem" className="form-label">
                   Conta Origem
                 </label>
                 <input
                   type="text"
                   id="contaOrigem"
                   name="contaOrigem"
                   value={formData.contaOrigem}
                   onChange={handleChange}
                   className="form-input"
                   placeholder="Digite o número da conta origem"
                   required
                 />
               </div>
               
               <div className="form-group">
                 <label htmlFor="contaDestino" className="form-label">
                   Conta Destino
                 </label>
                 <input
                   type="text"
                   id="contaDestino"
                   name="contaDestino"
                   value={formData.contaDestino}
                   onChange={handleChange}
                   className="form-input"
                   placeholder="Digite o número da conta destino"
                   required
                 />
               </div>
             </div>
            
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="valor" className="form-label">
                  Valor
                </label>
                <input
                  type="number"
                  id="valor"
                  name="valor"
                  value={formData.valor}
                  onChange={handleChange}
                  className="form-input"
                  placeholder="0,00"
                  step="0.01"
                  min="0.01"
                  required
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="descricao" className="form-label">
                  Descrição
                </label>
                <input
                  type="text"
                  id="descricao"
                  name="descricao"
                  value={formData.descricao}
                  onChange={handleChange}
                  className="form-input"
                  placeholder="Descrição da transferência"
                  required
                />
              </div>
            </div>
            
            <div className="form-actions">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setShowForm(false)}
              >
                Cancelar
              </button>
              <button
                type="submit"
                className="btn btn-primary"
                disabled={loading}
              >
                {loading ? 'Processando...' : 'Realizar Transferência'}
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="card">
        <h3 style={{ marginBottom: '24px', color: 'var(--text-primary)' }}>
          Histórico de Transações
        </h3>
        
        {transacoes.length > 0 ? (
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
                {transacoes.map((transacao) => (
                  <tr key={transacao.id}>
                    <td>{formatDate(transacao.realizadaEm)}</td>
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
                      <span style={{ 
                        padding: '4px 8px', 
                        borderRadius: '4px', 
                        fontSize: '12px',
                        background: '#d1fae5',
                        color: '#059669'
                      }}>
                        CONCLUÍDA
                      </span>
                    </td>
                    <td>{transacao.descricao || 'Transferência entre contas'}</td>
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
          Tipos de Transação
        </h3>
        
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '24px' }}>
          <div style={{ padding: '16px', background: 'var(--background-light)', borderRadius: '8px' }}>
            <h4 style={{ marginBottom: '8px', color: 'var(--text-primary)' }}>
              💸 Transferência
            </h4>
            <p style={{ margin: 0, fontSize: '14px', color: 'var(--text-secondary)' }}>
              Transferência entre contas do mesmo banco
            </p>
          </div>
          
          <div style={{ padding: '16px', background: 'var(--background-light)', borderRadius: '8px' }}>
            <h4 style={{ marginBottom: '8px', color: 'var(--text-primary)' }}>
              📱 PIX
            </h4>
            <p style={{ margin: 0, fontSize: '14px', color: 'var(--text-secondary)' }}>
              Transferência instantânea via PIX
            </p>
          </div>
          
          <div style={{ padding: '16px', background: 'var(--background-light)', borderRadius: '8px' }}>
            <h4 style={{ marginBottom: '8px', color: 'var(--text-primary)' }}>
              💰 Depósito
            </h4>
            <p style={{ margin: 0, fontSize: '14px', color: 'var(--text-secondary)' }}>
              Depósito em conta bancária
            </p>
          </div>
          
          <div style={{ padding: '16px', background: 'var(--background-light)', borderRadius: '8px' }}>
            <h4 style={{ marginBottom: '8px', color: 'var(--text-primary)' }}>
              💳 Saque
            </h4>
            <p style={{ margin: 0, fontSize: '14px', color: 'var(--text-secondary)' }}>
              Saque de valores da conta
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Transacoes; 