/**
 * Camada de comunicação com a API do backend (porta 8080).
 *
 * O Vite faz o proxy de /api → http://localhost:8080 (configurado em vite.config.js),
 * então no código chamamos apenas "/api/..." sem precisar do host completo.
 *
 * Cada grupo de funções corresponde a um controller do backend:
 *   authApi      → AuthController    (/api/auth/...)
 *   restauranteApi → RestauranteController + CardapioController
 *   pedidoApi    → PedidoController  (/api/pedidos/...)
 *   statusApi    → StatusController  (/api/status/...)
 */

import axios from 'axios'

// instância base do Axios com o prefixo /api
const api = axios.create({ baseURL: '/api' })

/** Autenticação (PostgreSQL) */
export const authApi = {
  login:    (data) => api.post('/auth/login', data),
  cadastro: (data) => api.post('/auth/cadastro', data),
}

/** Restaurantes e cardápios (PostgreSQL + MongoDB) */
export const restauranteApi = {
  listar:   ()    => api.get('/restaurantes'),
  buscar:   (id)  => api.get(`/restaurantes/${id}`),
  produtos: (id)  => api.get(`/restaurantes/${id}/produtos`), // fallback PostgreSQL
  cardapio: (id)  => api.get(`/cardapios/restaurante/${id}`), // preferencial MongoDB
}

/** Pedidos (PostgreSQL + Cassandra) */
export const pedidoApi = {
  criar:           (data)       => api.post('/pedidos', data),
  listarPorUsuario:(usuarioId)  => api.get(`/pedidos/usuario/${usuarioId}`),
  buscar:          (id)         => api.get(`/pedidos/${id}`),
  itens:           (id)         => api.get(`/pedidos/${id}/itens`),
}

/** Rastreamento e logs (Cassandra) */
export const statusApi = {
  buscar:   (pedidoId)                    => api.get(`/status/pedido/${pedidoId}`),
  atualizar:(pedidoId, status, local = '') =>
    api.put(`/status/pedido/${pedidoId}?status=${encodeURIComponent(status)}&localizacao=${encodeURIComponent(local)}`),
  eventos:  (pedidoId)                    => api.get(`/status/pedido/${pedidoId}/eventos`),
  logs:     (servico = 'PedidoService')   => api.get(`/status/logs?servico=${encodeURIComponent(servico)}`),
}

export default api
