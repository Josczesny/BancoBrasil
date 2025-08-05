# �� Sistema Bancário - Backend API

Sistema bancário robusto inspirado no Banco do Brasil, com foco no desenvolvimento de APIs RESTful utilizando Java e Spring Boot. O projeto implementa padrões de segurança, escalabilidade e conformidade com regulamentações do mercado financeiro brasileiro.

## 🎯 Foco do Projeto

Este projeto é **primariamente focado no desenvolvimento backend**, oferecendo uma API RESTful completa para operações bancárias. O frontend React é fornecido como interface complementar para demonstração das funcionalidades da API.

## 🚀 Stack Tecnológica

### Backend (Principal)
- **Java 17** - Linguagem principal com recursos modernos
- **Spring Boot 3.2.0** - Framework para desenvolvimento de APIs REST
- **Spring Security** - Autenticação e autorização robusta
- **JWT (JSON Web Tokens)** - Autenticação stateless
- **PostgreSQL** - Banco de dados relacional de alta performance
- **JPA/Hibernate** - ORM para mapeamento objeto-relacional
- **BCrypt** - Criptografia segura de senhas
- **Maven** - Gerenciamento de dependências e build

### Frontend (Complementar)
- **React 18** - Interface de usuário para demonstração
- **React Router** - Navegação SPA
- **CSS3** - Estilização moderna e responsiva

### Infraestrutura
- **Docker** - Containerização para deploy
- **Git** - Controle de versão distribuído

## 📋 Funcionalidades da API

### 🔐 Autenticação e Segurança
- **Login/Logout** com JWT
- **Refresh Tokens** para renovação automática
- **Validação de tokens** em todas as requisições
- **Criptografia AES** para dados sensíveis
- **BCrypt** para hash de senhas
- **Logs de auditoria** para compliance

### 💰 Gestão de Contas
- **CRUD completo** de contas bancárias
- **Múltiplos tipos** de conta (Corrente, Poupança)
- **Gestão de agências** e números de conta
- **Limites de crédito** configuráveis
- **Histórico de criação** e modificações

### 💸 Transações Bancárias
- **Transferências** entre contas
- **Depósitos** e saques
- **Transações PIX** (estrutura preparada)
- **Validações** de saldo e limites
- **Histórico completo** de transações
- **Rollback automático** em caso de falha

### 📊 Dashboard e Relatórios
- **Resumo financeiro** em tempo real
- **Estatísticas** de transações
- **Indicadores** de performance
- **Logs de auditoria** estruturados

## 🛠️ Instalação e Configuração

### Pré-requisitos
- **Java 17** ou superior
- **PostgreSQL 12** ou superior
- **Maven 3.6** ou superior
- **Node.js 16** ou superior (apenas para frontend)

### 1. Clone o repositório
```bash
git clone https://github.com/Josczesny/BancoBrasil.git
cd BancoBrasil
```

### 2. Configuração do Banco de Dados

#### PostgreSQL
```bash
# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib

# macOS
brew install postgresql

# Windows
# Baixe do site oficial: https://www.postgresql.org/download/windows/
```

#### Crie o banco de dados
```bash
# Acesse o PostgreSQL
sudo -u postgres psql

# Crie o banco
CREATE DATABASE bancobr;

# Crie o usuário (opcional)
CREATE USER bancobr WITH PASSWORD 'bancobr123';
GRANT ALL PRIVILEGES ON DATABASE bancobr TO bancobr;

# Saia do PostgreSQL
\q
```

#### Execute os scripts SQL
```bash
# Execute o script de inicialização
psql -U postgres -d bancobr -f database/schema.sql
psql -U postgres -d bancobr -f database/seed.sql
```

### 3. Configuração do Backend

#### Configure as variáveis de ambiente
```bash
# Edite o arquivo backend/src/main/resources/application.properties
# Ajuste as configurações do banco de dados conforme necessário
```

#### Execute o backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080/api`

### 4. Configuração do Frontend (Opcional)

#### Instale as dependências
```bash
cd frontend
npm install
```

#### Execute o frontend
```bash
npm start
```

O frontend estará disponível em: `http://localhost:3000`

## 🔑 Dados de Teste

### Usuários Disponíveis

#### Administrador
- **Email:** admin@bancobr.com
- **Senha:** admin123
- **Tipo:** ADMIN

#### Cliente
- **Email:** joao.silva@email.com
- **Senha:** cliente123
- **Tipo:** CLIENTE

### Contas de Teste
- Contas correntes e poupança criadas automaticamente
- Saldo inicial: R$ 50.000,00
- Limite de crédito: R$ 5.000,00

## 📚 Documentação da API

### Autenticação
```
POST /api/auth/login          # Login de usuário
POST /api/auth/logout         # Logout seguro
POST /api/auth/refresh        # Renovar token
GET  /api/auth/validate       # Validar token
```

### Transações
```
POST /api/transacoes/transferencia    # Transferência entre contas
POST /api/transacoes/deposito         # Depósito em conta
POST /api/transacoes/saque            # Saque de conta
GET  /api/transacoes/conta/{numero}   # Histórico de transações
GET  /api/transacoes/{id}             # Detalhes de transação
```

### Contas
```
GET /api/contas/usuario       # Contas do usuário logado
GET /api/contas/dashboard     # Dados para dashboard
GET /api/contas/{numero}      # Detalhes de conta específica
```

### Usuários
```
GET /api/usuarios/perfil      # Perfil do usuário logado
PUT /api/usuarios/perfil      # Atualizar perfil
```

## 🔒 Segurança e Conformidade

### Implementações de Segurança
- **Criptografia AES** para dados sensíveis
- **BCrypt** para hash de senhas (custo 10)
- **JWT** com expiração configurável (24h)
- **Validação de entrada** em todos os endpoints
- **Logs de auditoria** para todas as transações
- **Rate limiting** para prevenir ataques
- **CORS** configurado adequadamente
- **Headers de segurança** (HSTS, CSP, etc.)

### Conformidade Regulatória
- **Open Banking** - APIs padronizadas
- **Febraban** - Padrões bancários brasileiros
- **LGPD** - Proteção de dados pessoais
- **PCI DSS** - Segurança de dados de pagamento
- **BCB** - Regulamentações do Banco Central

## 🏗️ Arquitetura

### Padrões de Projeto
- **MVC** - Model-View-Controller
- **Repository Pattern** - Acesso a dados
- **Service Layer** - Lógica de negócio
- **DTO Pattern** - Transferência de dados
- **Factory Pattern** - Criação de objetos
- **Strategy Pattern** - Algoritmos de validação

### Estrutura do Projeto
```
backend/
├── src/main/java/com/bancobr/
│   ├── config/          # Configurações (Security, JWT, etc.)
│   ├── controller/      # Controllers REST
│   ├── dto/            # Data Transfer Objects
│   ├── model/          # Entidades JPA
│   ├── repository/     # Repositories JPA
│   ├── security/       # Componentes de segurança
│   └── service/        # Serviços de negócio
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

### Produção
```bash
# Backend
cd backend
mvn clean package -DskipTests
java -jar target/banking-system-1.0.0.jar

# Frontend (opcional)
cd frontend
npm run build
# Sirva os arquivos estáticos com nginx
```

## 📊 Monitoramento

### Health Checks
- Backend: `http://localhost:8080/api/actuator/health`
- Database: `http://localhost:8080/api/actuator/health/db`

### Logs
- Logs estruturados em JSON
- Níveis configuráveis (DEBUG, INFO, WARN, ERROR)
- Rotação automática de logs
- Integração com ELK Stack (configurável)

## 🧪 Testes

### Executar Testes
```bash
# Testes unitários
mvn test

# Testes de integração
mvn verify

# Cobertura de código
mvn jacoco:report
```

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/NovaFuncionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 📞 Suporte

- **Issues:** [GitHub Issues](https://github.com/Josczesny/BancoBrasil/issues)
- **Documentação:** [Wiki do Projeto](https://github.com/Josczesny/BancoBrasil/wiki)

## 🏆 Características Técnicas

### Performance
- **Connection Pool** - HikariCP otimizado
- **Cache** - Redis (configurável)
- **Compressão** - GZIP automática
- **Async Processing** - Operações assíncronas

### Escalabilidade
- **Horizontal Scaling** - Múltiplas instâncias
- **Load Balancing** - Distribuição de carga
- **Database Sharding** - Particionamento de dados
- **Message Queue** - Kafka (preparado para futuro)

### Observabilidade
- **Micrometer** - Métricas de aplicação
- **Prometheus** - Coleta de métricas
- **Grafana** - Visualização de dados
- **Distributed Tracing** - Rastreamento distribuído

---

*Este projeto demonstra as melhores práticas de desenvolvimento backend para sistemas financeiros, com foco em segurança, performance e conformidade regulatória.*

