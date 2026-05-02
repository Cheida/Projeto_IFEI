# Projeto_IFEI

## Integrantes
- Bruno
- Gustavo
- Victor

### Sobre o Projeto

Esse projeto consiste no desenvolvimento de um sistema de delivery, onde o usuário pode fazer pedidos, visualizar produtos e acompanhar o status da entrega.
A ideia principal é construir um sistema mais próximo do mundo real, utilizando mais de um tipo de banco de dados, aproveitando o que cada um tem de melhor.

### Objetivo

O objetivo desse trabalho é:
- Aplicar conceitos de banco de dados (relacional e não relacional)
- Entender quando usar cada tipo de banco
- Desenvolver um backend que consiga integrar tudo
- Simular um sistema real de delivery

## Bancos de Dados Utilizados
### PostgreSQL

Vai ser usado para guardar os dados principais do sistema, como:
- Usuários
- Restaurantes
- Produtos
- Pedidos

### Por que usar?
- Porque é um banco relacional, então funciona muito bem com dados organizados e com relacionamento entre tabelas.

### MongoDB

Vai ser usado para dados mais flexíveis, como:
- Cardápio de comida

### Por que usar?
- Porque ele trabalha com documentos (tipo JSON), então é mais fácil de adaptar conforme o sistema cresce.

### Cassandra

Vai ser usado para dados que mudam o tempo todo, como:
- Status de entrega
- Logs do sistema
- Eventos dos pedidos

### Por que usar?
- Porque é um banco muito rápido para escrita e funciona bem com grandes volumes de dados.

### Backend

O backend vai ser responsável por:
- Receber as requisições do frontend
- Processar as regras de negócio
- Decidir qual banco usar em cada caso
  
-> Funcionamento básico:
- O usuário faz uma ação (ex: pedir comida)
- O backend recebe isso
- Ele salva ou busca dados no banco correto
- Retorna a resposta pro frontend
  
-> Tecnologias (previstas):
- Node.js ou Java
- API REST

### Frontend

O frontend será a parte visual do sistema, onde o usuário vai poder:
- Se cadastrar e logar
- Ver produtos
- Fazer pedidos
- Acompanhar a entrega

### Observação (CAP)

Cada banco foi escolhido também pensando no equilíbrio entre:
- Consistência
- Disponibilidade
- Tolerância a falhas

Exemplo:
- PostgreSQL → mais consistente
- Cassandra → mais disponível
- MongoDB → meio termo

---

## O que foi implementado

O backend e o frontend foram desenvolvidos do zero. Abaixo está um resumo completo do que foi feito e como tudo se conecta.

### Tecnologias usadas

| Camada    | Tecnologia                        |
|-----------|-----------------------------------|
| Backend   | Java 17 + Spring Boot 3.2         |
| Frontend  | React 18 + Vite + React Router v6 |
| HTTP      | Axios (chamadas do front pro back) |
| Build     | Maven (backend) / npm (frontend)  |

### Estrutura de pastas

```
Projeto_IFEI-main/
├── backend/                        → API REST em Java
│   └── src/main/java/com/delivery/
│       ├── config/                 → Configurações (CORS, bancos)
│       ├── controller/             → Endpoints HTTP (recebe as requisições)
│       ├── dto/                    → Objetos de entrada/saída da API
│       ├── model/
│       │   ├── postgres/           → Entidades JPA (tabelas do PostgreSQL)
│       │   ├── mongo/              → Documento MongoDB (cardápio)
│       │   └── cassandra/          → Tabelas Cassandra (status, eventos, logs)
│       ├── repository/             → Acesso ao banco (Spring Data)
│       │   ├── postgres/
│       │   ├── mongo/
│       │   └── cassandra/
│       └── service/                → Regras de negócio
│
├── frontend/                       → Interface React
│   └── src/
│       ├── context/                → Estado global (usuário logado)
│       ├── services/               → Funções de chamada à API
│       ├── components/             → Componentes reutilizáveis (Navbar)
│       └── pages/                  → Telas do sistema
│
├── cardapios.seed.json             → Dados iniciais do MongoDB
├── Código_das_tabelas_cassandra    → DDL das tabelas Cassandra
└── Tabelas e Inserção de Dados...  → DDL + seed do PostgreSQL
```

### Quem faz o quê (por banco)

| Ação do usuário                    | Banco usado              |
|------------------------------------|--------------------------|
| Login e cadastro                   | PostgreSQL (Supabase)    |
| Listar restaurantes                | PostgreSQL (Supabase)    |
| Ver cardápio com categorias        | MongoDB                  |
| Ver produtos simples               | PostgreSQL (Supabase)    |
| Fazer um pedido                    | PostgreSQL + Cassandra   |
| Atualizar status do pedido         | PostgreSQL + Cassandra   |
| Rastrear pedido (histórico)        | Cassandra                |
| Ver eventos do pedido              | Cassandra                |
| Logs do sistema                    | Cassandra                |

### Endpoints da API (porta 8080)

| Método | Rota                              | Descrição                            |
|--------|-----------------------------------|--------------------------------------|
| POST   | /api/auth/login                   | Login do usuário                     |
| POST   | /api/auth/cadastro                | Cadastro de novo usuário             |
| GET    | /api/restaurantes                 | Lista todos os restaurantes          |
| GET    | /api/restaurantes/{id}            | Busca um restaurante por ID          |
| GET    | /api/restaurantes/{id}/produtos   | Lista produtos do restaurante (PostgreSQL) |
| GET    | /api/cardapios/restaurante/{id}   | Busca cardápio do restaurante (MongoDB) |
| POST   | /api/pedidos                      | Cria um novo pedido                  |
| GET    | /api/pedidos/usuario/{id}         | Lista pedidos de um usuário          |
| GET    | /api/pedidos/{id}                 | Busca um pedido por ID               |
| GET    | /api/pedidos/{id}/itens           | Lista itens de um pedido             |
| GET    | /api/status/pedido/{id}           | Histórico de status (Cassandra)      |
| PUT    | /api/status/pedido/{id}           | Atualiza status do pedido            |
| GET    | /api/status/pedido/{id}/eventos   | Eventos do pedido (Cassandra)        |
| GET    | /api/status/logs                  | Logs do sistema (Cassandra)          |

### Telas do sistema (frontend, porta 5173)

| Rota                  | Tela                                          |
|-----------------------|-----------------------------------------------|
| /login                | Login com email e senha                       |
| /cadastro             | Criação de nova conta                         |
| /                     | Lista de restaurantes                         |
| /restaurante/:id      | Cardápio do restaurante + carrinho + pedido   |
| /pedidos              | Lista de pedidos do usuário logado            |
| /tracking/:pedidoId   | Rastreamento do pedido em tempo real          |

---

## Como rodar o projeto

### Pré-requisitos

- Java 17+
- Maven
- Node.js 18+
- PostgreSQL via Supabase (já configurado com os dados do projeto)
- MongoDB rodando localmente (porta 27017)
- Apache Cassandra rodando localmente (porta 9042)

### Passo 1 — Configurar o backend

Abra o arquivo `backend/src/main/resources/application.properties` e substitua:

```properties
spring.datasource.url=jdbc:postgresql://db.SEU_PROJETO.supabase.co:5432/postgres?sslmode=require
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA_SUPABASE
```

### Passo 2 — Preparar o Cassandra

Com o Cassandra rodando, execute o script de criação do keyspace e tabelas:

```bash
cqlsh -f backend/src/main/resources/cassandra_init.cql
```

### Passo 3 — Importar dados no MongoDB

```bash
mongoimport --db delivery_db --collection cardapios --file cardapios.seed.json --jsonArray
```

### Passo 4 — Rodar o backend

```bash
cd backend
./mvnw spring-boot:run
# Windows: mvnw.cmd spring-boot:run
```

### Passo 5 — Rodar o frontend

```bash
cd frontend
npm install
npm run dev
```

Acesse: **http://localhost:5173**

Use qualquer usuário do seed (ex: `gustavo1@email.com` / senha: `123`)

---

## O que ainda pode ser melhorado (trabalhos futuros)

- Autenticação com JWT (token de segurança)
- Criptografia da senha (BCrypt)
- Paginação na listagem de restaurantes e pedidos
- Imagens dos produtos
- Filtro por categoria de restaurante
- Notificação em tempo real do status (WebSocket)
- Deploy em nuvem (Railway, Render, Vercel)
