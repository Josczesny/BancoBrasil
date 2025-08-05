import React from 'react';

const Footer = () => {
  return (
    <footer className="footer">
      <div className="container">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px' }}>
          <div>
            <p style={{ margin: 0, fontSize: '14px' }}>
              © 2024 Sistema Bancário - Banco do Brasil. Todos os direitos reservados.
            </p>
          </div>
          
          <div style={{ display: 'flex', gap: '24px', fontSize: '12px', opacity: 0.8 }}>
            <span>Segurança</span>
            <span>Privacidade</span>
            <span>Termos de Uso</span>
            <span>Suporte</span>
          </div>
        </div>
        
        <div style={{ marginTop: '16px', textAlign: 'center', fontSize: '12px', opacity: 0.6 }}>
          <p style={{ margin: 0 }}>
            Sistema desenvolvido com tecnologias modernas: React, Spring Boot e PostgreSQL
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer; 