/**
 * Tela "Meus Pedidos".
 *
 * Fluxo:
 *   1. Lê o usuário logado pelo AuthContext
 *   2. Busca no backend todos os pedidos daquele usuário: GET /api/pedidos/usuario/{id}
 *   3. Mostra o status atual salvo no PostgreSQL
 *   4. Permite abrir a tela de rastreamento, onde o histórico vem do Cassandra
 *
 * A tela também trata o caso em que o usuário ainda não realizou pedidos.
 */

import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { pedidoApi } from '../services/api'
import { useAuth } from '../context/AuthContext'

// Mapa usado para transformar o status técnico do banco em texto, cor e ícone na interface.
const STATUS_INFO = {
  pendente:   { label: 'Pendente',   cor: '#f39c12', icone: '⏳' },
  preparando: { label: 'Preparando', cor: '#3498db', icone: '👨‍🍳' },
  entregue:   { label: 'Entregue',   cor: '#27ae60', icone: '✅' },
}

export default function MeusPedidos() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [pedidos, setPedidos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')

  useEffect(() => {
    // Busca somente os pedidos do usuário autenticado.
    pedidoApi.listarPorUsuario(user.id)
      .then(res => setPedidos(res.data))
      .catch(() => setErro('Erro ao carregar pedidos'))
      .finally(() => setCarregando(false))
  }, [user.id])

  if (carregando) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Carregando pedidos...</p>
      </div>
    )
  }

  return (
    <div className="container">
      <div className="page-header">
        <h1>Meus Pedidos</h1>
        <p>Acompanhe seus pedidos</p>
      </div>

      {erro && <div className="erro-msg">{erro}</div>}

      {pedidos.length === 0 ? (
        <div className="empty-state">
          <div className="empty-icone">🛒</div>
          <p>Você ainda não fez nenhum pedido.</p>
          <button className="btn-primary" onClick={() => navigate('/')}>Explorar restaurantes</button>
        </div>
      ) : (
        <div className="pedidos-lista">
          {pedidos.map(pedido => {
            // Fallback evita quebrar a tela caso apareça um status novo no banco.
            const info = STATUS_INFO[pedido.status] || { label: pedido.status, cor: '#999', icone: '📦' }
            return (
              <div key={pedido.id} className="pedido-card">
                <div className="pedido-card-info">
                  <div className="pedido-card-header">
                    <h3>Pedido #{pedido.id}</h3>
                    <span className="status-badge" style={{ backgroundColor: info.cor }}>
                      {info.icone} {info.label}
                    </span>
                  </div>
                  <p className="pedido-restaurante">{pedido.restaurante?.nome}</p>
                  <p className="pedido-total">R$ {parseFloat(pedido.total || 0).toFixed(2)}</p>
                  {pedido.dataHora && (
                    <p className="pedido-data">
                      {new Date(pedido.dataHora).toLocaleString('pt-BR')}
                    </p>
                  )}
                </div>
                <button
                  className="btn-tracking"
                  onClick={() => navigate(`/tracking/${pedido.id}`)}
                >
                  Acompanhar →
                </button>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
