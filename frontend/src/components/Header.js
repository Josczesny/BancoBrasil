import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const Header = ({ isAuthenticated, user, onLogout }) => {
  const location = useLocation();

  const handleLogout = async () => {
    try {
      const token = localStorage.getItem('token');
      if (token) {
        await fetch('/api/auth/logout', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
      }
    } catch (error) {
      console.error('Erro ao fazer logout:', error);
    } finally {
      onLogout();
    }
  };

  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
    <header className="header">
      <div className="container">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '32px' }}>
            <Link to="/" style={{ textDecoration: 'none', color: 'white' }}>
              <h1>🏦 Banco do Brasil</h1>
            </Link>
            
            {isAuthenticated && (
              <nav style={{ display: 'flex', gap: '24px' }}>
                <Link 
                  to="/dashboard" 
                  style={{ 
                    textDecoration: 'none', 
                    color: isActive('/dashboard') ? 'var(--primary-color)' : 'white',
                    fontWeight: isActive('/dashboard') ? '600' : '400',
                    padding: '8px 16px',
                    borderRadius: '6px',
                    transition: 'all 0.2s ease'
                  }}
                >
                  Dashboard
                </Link>
                <Link 
                  to="/contas" 
                  style={{ 
                    textDecoration: 'none', 
                    color: isActive('/contas') ? 'var(--primary-color)' : 'white',
                    fontWeight: isActive('/contas') ? '600' : '400',
                    padding: '8px 16px',
                    borderRadius: '6px',
                    transition: 'all 0.2s ease'
                  }}
                >
                  Contas
                </Link>
                <Link 
                  to="/transacoes" 
                  style={{ 
                    textDecoration: 'none', 
                    color: isActive('/transacoes') ? 'var(--primary-color)' : 'white',
                    fontWeight: isActive('/transacoes') ? '600' : '400',
                    padding: '8px 16px',
                    borderRadius: '6px',
                    transition: 'all 0.2s ease'
                  }}
                >
                  Transações
                </Link>
              </nav>
            )}
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            {isAuthenticated && user ? (
              <>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: '14px', opacity: 0.9 }}>
                    Olá, {user.nome}
                  </div>
                  <div style={{ fontSize: '12px', opacity: 0.7 }}>
                    {user.tipo === 'ADMIN' ? 'Administrador' : 'Cliente'}
                  </div>
                </div>
                <button 
                  onClick={handleLogout}
                  style={{
                    background: 'rgba(255, 255, 255, 0.2)',
                    border: '1px solid rgba(255, 255, 255, 0.3)',
                    color: 'white',
                    padding: '8px 16px',
                    borderRadius: '6px',
                    cursor: 'pointer',
                    fontSize: '14px',
                    transition: 'all 0.2s ease'
                  }}
                  onMouseOver={(e) => {
                    e.target.style.background = 'rgba(255, 255, 255, 0.3)';
                  }}
                  onMouseOut={(e) => {
                    e.target.style.background = 'rgba(255, 255, 255, 0.2)';
                  }}
                >
                  Sair
                </button>
              </>
            ) : (
              <Link 
                to="/login"
                style={{
                  background: 'var(--primary-color)',
                  color: 'var(--text-primary)',
                  padding: '8px 16px',
                  borderRadius: '6px',
                  textDecoration: 'none',
                  fontWeight: '500',
                  transition: 'all 0.2s ease'
                }}
                onMouseOver={(e) => {
                  e.target.style.background = '#e6c200';
                }}
                onMouseOut={(e) => {
                  e.target.style.background = 'var(--primary-color)';
                }}
              >
                Entrar
              </Link>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header; 