import React, { useState, useEffect } from 'react';
import SaldoDisplay from '../components/SaldoDisplay';

const Contas = ({ user }) => {
  const [usuarios, setUsuarios] = useState([]);
  const [contas, setContas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Estados para filtros e ordenação
  const [sortConfig, setSortConfig] = useState({});
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    if (user?.tipo === 'ADMIN') {
      fetchAdminData();
    } else {
      fetchUserContas();
    }
  }, [user]);

  const fetchAdminData = async () => {
    try {
      const token = localStorage.getItem('token');
      
      // Buscar usuários
      const usuariosResponse = await fetch('/api/usuarios', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      // Buscar contas
      const contasResponse = await fetch('/api/contas', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (usuariosResponse.ok && contasResponse.ok) {
        const usuariosData = await usuariosResponse.json();
        const contasData = await contasResponse.json();
        
        // Combinar dados de usuários e contas
        const usuariosComContas = usuariosData.map(usuario => {
          const contasDoUsuario = contasData.filter(conta => conta.usuario?.id === usuario.id);
          const saldoTotal = contasDoUsuario.reduce((total, conta) => total + parseFloat(conta.saldo || 0), 0);
          
          return {
            ...usuario,
            contas: contasDoUsuario,
            saldoTotal: saldoTotal,
            contasAtivas: contasDoUsuario.filter(conta => conta.ativa).length,
            totalContas: contasDoUsuario.length
          };
        });

        setUsuarios(usuariosComContas);
        setContas(contasData);
      } else {
        setError('Erro ao carregar dados administrativos');
      }
    } catch (error) {
      setError('Erro de conexão');
    } finally {
      setLoading(false);
    }
  };

  const fetchUserContas = async () => {
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

  // Função para ordenação
  const handleSort = (column) => {
    let direction = 'asc';
    
    if (sortConfig.column === column) {
      if (sortConfig.direction === 'asc') {
        direction = 'desc';
      } else if (sortConfig.direction === 'desc') {
        direction = null; // Remove filtro
      }
    }
    
    setSortConfig(direction ? { column, direction } : {});
  };

  // Função para obter ícone de ordenação
  const getSortIcon = (column) => {
    if (sortConfig.column !== column) {
      return '↕️';
    }
    return sortConfig.direction === 'asc' ? '↑' : '↓';
  };

  // Função para aplicar filtros e ordenação
  const getFilteredAndSortedData = () => {
    let data = [...usuarios];

    // Aplicar busca
    if (searchTerm) {
      data = data.filter(usuario => 
        usuario.nome?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        usuario.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        usuario.cpf?.includes(searchTerm)
      );
    }

    // Aplicar ordenação
    if (sortConfig.column && sortConfig.direction) {
      data.sort((a, b) => {
        let aValue = a[sortConfig.column];
        let bValue = b[sortConfig.column];

        // Tratamento especial para valores numéricos
        if (sortConfig.column === 'saldoTotal') {
          aValue = parseFloat(aValue || 0);
          bValue = parseFloat(bValue || 0);
        }

        if (aValue < bValue) {
          return sortConfig.direction === 'asc' ? -1 : 1;
        }
        if (aValue > bValue) {
          return sortConfig.direction === 'asc' ? 1 : -1;
        }
        return 0;
      });
    }

    return data;
  };

  // Paginação
  const filteredData = getFilteredAndSortedData();
  const totalPages = Math.ceil(filteredData.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentData = filteredData.slice(startIndex, endIndex);

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value || 0);
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('pt-BR');
  };

  const formatCPF = (cpf) => {
    if (!cpf) return '-';
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '40px' }}>
        <div className="spinner"></div>
        <p>Carregando dados...</p>
      </div>
    );
  }

  // Renderização para usuários comuns
  if (user?.tipo !== 'ADMIN') {
    return (
      <div className="fade-in">
        <div style={{ marginBottom: '32px' }}>
          <h1 style={{ marginBottom: '8px', color: 'var(--text-primary)' }}>
            Minhas Contas
          </h1>
          <p style={{ color: 'var(--text-secondary)', margin: 0 }}>
            Gerencie suas contas bancárias
          </p>
        </div>

        {error && (
          <div className="alert alert-error">
            {error}
          </div>
        )}

        <SaldoDisplay user={user} />

        <div className="dashboard-grid">
          {contas.map((conta) => (
            <div key={conta.id} className="dashboard-card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '16px' }}>
                <h3 style={{ margin: 0 }}>
                  {conta.tipoConta === 'CORRENTE' ? '🏦' : '💰'} {conta.tipoConta}
                </h3>
                <span className={`status status-${conta.ativa ? 'success' : 'error'}`}>
                  {conta.ativa ? 'Ativa' : 'Inativa'}
                </span>
              </div>
              
              <div className="value" style={{ fontSize: '28px', marginBottom: '16px' }}>
                {formatCurrency(conta.saldo)}
              </div>
              
              <div style={{ marginBottom: '16px' }}>
                <div style={{ fontSize: '14px', color: 'var(--text-secondary)', marginBottom: '4px' }}>
                  Número da Conta
                </div>
                <div style={{ fontFamily: 'monospace', fontSize: '16px', fontWeight: '600' }}>
                  {conta.numeroConta}
                </div>
              </div>
              
              <div style={{ marginBottom: '16px' }}>
                <div style={{ fontSize: '14px', color: 'var(--text-secondary)', marginBottom: '4px' }}>
                  Agência
                </div>
                <div style={{ fontSize: '14px' }}>
                  {conta.agencia?.nome} ({conta.agencia?.codigo})
                </div>
              </div>
              
              <div style={{ marginBottom: '16px' }}>
                <div style={{ fontSize: '14px', color: 'var(--text-secondary)', marginBottom: '4px' }}>
                  Limite de Crédito
                </div>
                <div style={{ fontSize: '14px', fontWeight: '500' }}>
                  {formatCurrency(conta.limiteCredito)}
                </div>
              </div>
              
              <div>
                <div style={{ fontSize: '14px', color: 'var(--text-secondary)', marginBottom: '4px' }}>
                  Criada em
                </div>
                <div style={{ fontSize: '14px' }}>
                  {formatDate(conta.criadaEm)}
                </div>
              </div>
            </div>
          ))}
        </div>

        {contas.length === 0 && (
          <div className="card" style={{ textAlign: 'center', padding: '40px' }}>
            <h3 style={{ marginBottom: '16px', color: 'var(--text-primary)' }}>
              Nenhuma conta encontrada
            </h3>
            <p style={{ color: 'var(--text-secondary)', margin: 0 }}>
              Entre em contato com sua agência para abrir uma conta.
            </p>
          </div>
        )}
      </div>
    );
  }

  // Renderização para administradores
  return (
    <div className="fade-in">
      <div style={{ marginBottom: '32px' }}>
        <h1 style={{ marginBottom: '8px', color: 'var(--text-primary)' }}>
          Gestão de Contas - Administração
        </h1>
        <p style={{ color: 'var(--text-secondary)', margin: 0 }}>
          Visualize e gerencie todas as contas dos usuários
        </p>
      </div>

      {error && (
        <div className="alert alert-error">
          {error}
        </div>
      )}

             {/* Barra de busca */}
       <div className="card search-container">
         <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
           <div style={{ flex: 1 }}>
             <input
               type="text"
               placeholder="Buscar por nome, email ou CPF..."
               value={searchTerm}
               onChange={(e) => setSearchTerm(e.target.value)}
               className="search-input"
             />
           </div>
           <div style={{ fontSize: '14px', color: 'var(--text-secondary)' }}>
             {filteredData.length} usuário(s) encontrado(s)
           </div>
         </div>
       </div>

             {/* Tabela de usuários */}
       <div className="card">
         <div className="table-container">
           <table className="table table-intelligent" style={{ width: '100%' }}>
             <thead>
               <tr>
                 <th 
                   onClick={() => handleSort('nome')}
                   className={`sortable ${sortConfig.column === 'nome' ? sortConfig.direction : ''}`}
                 >
                   <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                     Nome 
                     <span className={`sort-icon ${sortConfig.column !== 'nome' ? 'neutral' : ''}`}>
                       {getSortIcon('nome')}
                     </span>
                   </div>
                 </th>
                 <th 
                   onClick={() => handleSort('email')}
                   className={`sortable ${sortConfig.column === 'email' ? sortConfig.direction : ''}`}
                 >
                   <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                     Email 
                     <span className={`sort-icon ${sortConfig.column !== 'email' ? 'neutral' : ''}`}>
                       {getSortIcon('email')}
                     </span>
                   </div>
                 </th>
                 <th 
                   onClick={() => handleSort('cpf')}
                   className={`sortable ${sortConfig.column === 'cpf' ? sortConfig.direction : ''}`}
                 >
                   <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                     CPF 
                     <span className={`sort-icon ${sortConfig.column !== 'cpf' ? 'neutral' : ''}`}>
                       {getSortIcon('cpf')}
                     </span>
                   </div>
                 </th>
                 <th 
                   onClick={() => handleSort('tipo')}
                   className={`sortable ${sortConfig.column === 'tipo' ? sortConfig.direction : ''}`}
                 >
                   <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                     Tipo 
                     <span className={`sort-icon ${sortConfig.column !== 'tipo' ? 'neutral' : ''}`}>
                       {getSortIcon('tipo')}
                     </span>
                   </div>
                 </th>
                 <th 
                   onClick={() => handleSort('saldoTotal')}
                   className={`sortable ${sortConfig.column === 'saldoTotal' ? sortConfig.direction : ''}`}
                 >
                   <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                     Saldo Total 
                     <span className={`sort-icon ${sortConfig.column !== 'saldoTotal' ? 'neutral' : ''}`}>
                       {getSortIcon('saldoTotal')}
                     </span>
                   </div>
                 </th>
                 <th 
                   onClick={() => handleSort('contasAtivas')}
                   className={`sortable ${sortConfig.column === 'contasAtivas' ? sortConfig.direction : ''}`}
                 >
                   <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                     Contas Ativas 
                     <span className={`sort-icon ${sortConfig.column !== 'contasAtivas' ? 'neutral' : ''}`}>
                       {getSortIcon('contasAtivas')}
                     </span>
                   </div>
                 </th>
                 <th 
                   onClick={() => handleSort('totalContas')}
                   className={`sortable ${sortConfig.column === 'totalContas' ? sortConfig.direction : ''}`}
                 >
                   <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                     Total Contas 
                     <span className={`sort-icon ${sortConfig.column !== 'totalContas' ? 'neutral' : ''}`}>
                       {getSortIcon('totalContas')}
                     </span>
                   </div>
                 </th>
                 <th 
                   onClick={() => handleSort('ativo')}
                   className={`sortable ${sortConfig.column === 'ativo' ? sortConfig.direction : ''}`}
                 >
                   <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                     Status 
                     <span className={`sort-icon ${sortConfig.column !== 'ativo' ? 'neutral' : ''}`}>
                       {getSortIcon('ativo')}
                     </span>
                   </div>
                 </th>
               </tr>
             </thead>
            <tbody>
              {currentData.map((usuario) => (
                <tr key={usuario.id}>
                  <td>
                    <div style={{ fontWeight: '600' }}>
                      {usuario.nome || '-'}
                    </div>
                  </td>
                  <td>{usuario.email || '-'}</td>
                  <td style={{ fontFamily: 'monospace' }}>
                    {formatCPF(usuario.cpf)}
                  </td>
                  <td>
                    <span style={{ 
                      padding: '4px 8px', 
                      borderRadius: '4px', 
                      fontSize: '12px',
                      background: usuario.tipo === 'ADMIN' ? '#fef3c7' : '#dbeafe',
                      color: usuario.tipo === 'ADMIN' ? '#d97706' : '#1e40af'
                    }}>
                      {usuario.tipo === 'ADMIN' ? 'Administrador' : 'Cliente'}
                    </span>
                  </td>
                  <td style={{ fontWeight: '600', color: usuario.saldoTotal >= 0 ? '#059669' : '#dc2626' }}>
                    {formatCurrency(usuario.saldoTotal)}
                  </td>
                  <td>
                    <span style={{ 
                      padding: '4px 8px', 
                      borderRadius: '4px', 
                      fontSize: '12px',
                      background: usuario.contasAtivas > 0 ? '#d1fae5' : '#fee2e2',
                      color: usuario.contasAtivas > 0 ? '#059669' : '#dc2626'
                    }}>
                      {usuario.contasAtivas}
                    </span>
                  </td>
                  <td>{usuario.totalContas}</td>
                  <td>
                    <span className={`status status-${usuario.ativo ? 'success' : 'error'}`}>
                      {usuario.ativo ? 'Ativo' : 'Inativo'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

                 {/* Paginação */}
         {totalPages > 1 && (
           <div className="pagination">
             <button
               onClick={() => setCurrentPage(Math.max(1, currentPage - 1))}
               disabled={currentPage === 1}
             >
               ← Anterior
             </button>

             {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
               <button
                 key={page}
                 onClick={() => setCurrentPage(page)}
                 className={currentPage === page ? 'active' : ''}
               >
                 {page}
               </button>
             ))}

             <button
               onClick={() => setCurrentPage(Math.min(totalPages, currentPage + 1))}
               disabled={currentPage === totalPages}
             >
               Próxima →
             </button>
           </div>
         )}

        {/* Informações da paginação */}
        <div style={{ 
          textAlign: 'center', 
          marginTop: '16px', 
          fontSize: '14px', 
          color: 'var(--text-secondary)' 
        }}>
          Mostrando {startIndex + 1} a {Math.min(endIndex, filteredData.length)} de {filteredData.length} usuários
        </div>
      </div>

             {/* Estatísticas */}
       <div className="card" style={{ marginTop: '24px' }}>
         <h3 style={{ marginBottom: '24px', color: 'var(--text-primary)' }}>
           Resumo Geral
         </h3>
         
         <div className="stats-grid">
          <div className="dashboard-card">
            <h3>👥 Total de Usuários</h3>
            <div className="value">{usuarios.length}</div>
            <div className="label">Cadastrados no sistema</div>
          </div>

          <div className="dashboard-card">
            <h3>🏦 Total de Contas</h3>
            <div className="value">{contas.length}</div>
            <div className="label">Contas bancárias</div>
          </div>

          <div className="dashboard-card">
            <h3>💰 Saldo Total</h3>
            <div className="value">
              {formatCurrency(usuarios.reduce((total, user) => total + (user.saldoTotal || 0), 0))}
            </div>
            <div className="label">Soma de todos os saldos</div>
          </div>

          <div className="dashboard-card">
            <h3>✅ Contas Ativas</h3>
            <div className="value">
              {contas.filter(conta => conta.ativa).length}
            </div>
            <div className="label">Contas em operação</div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Contas; 