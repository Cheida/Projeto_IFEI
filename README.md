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
