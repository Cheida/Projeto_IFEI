/**
 * Tela de rastreamento do pedido.
 *
 * Fluxo:
 *   1. Lê o id do pedido pela URL (/tracking/:pedidoId)
 *   2. Busca o pedido atual no PostgreSQL
 *   3. Busca histórico de status e eventos no Cassandra
 *   4. Atualiza automaticamente os dados a cada 15 segundos
 *   5. Permite alterar o status do pedido, gravando no PostgreSQL e no Cassandra
 *
 * O Cassandra é usado como histórico/eventos; se estiver indisponível, a tela ainda exibe o pedido.
 */

import { useState, useEffect, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { statusApi, pedidoApi } from '../services/api'

// Ordem oficial dos estados mostrados na linha de progresso.
const STEPS = ['pendente', 'preparando', 'entregue']

// Textos e locais usados tanto para renderizar a tela quanto para atualizar o backend.
const STEP_INFO = {
  pendente:   { label: 'Pendente',   icone: '⏳', local: 'Restaurante' },
  preparando: { label: 'Preparando', icone: '👨‍🍳', local: 'Em preparação' },
  entregue:   { label: 'Entregue',   icone: '✅', local: 'Entregue ao cliente' },
}

export default function Tracking() {
  const { pedidoId } = useParams()
  const navigate = useNavigate()

  const [pedido, setPedido] = useState(null)
  const [statusList, setStatusList] = useState([])
  const [eventos, setEventos] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [atualizando, setAtualizando] = useState(false)
  const [erro, setErro] = useState('')

  const carregar = useCallback(async () => {
    try {
      // Status e eventos são opcionais porque dependem do Cassandra estar ativo.
      const [pedRes, statusRes, eventosRes] = await Promise.all([
        pedidoApi.buscar(pedidoId),
        statusApi.buscar(pedidoId).catch(() => ({ data: [] })),
        statusApi.eventos(pedidoId).catch(() => ({ data: [] })),
      ])
      setPedido(pedRes.data)
      setStatusList(statusRes.data)
      setEventos(eventosRes.data)
    } catch {
      setErro('Erro ao carregar dados do pedido')
    } finally {
      setCarregando(false)
    }
  }, [pedidoId])

  useEffect(() => {
    carregar()
    // Polling simples para simular atualização em tempo real sem WebSocket.
    const interval = setInterval(carregar, 15000)
    return () => clearInterval(interval)
  }, [carregar])

  async function atualizarStatus(novoStatus) {
    setAtualizando(true)
    setErro('')
    try {
      // O service do backend sincroniza o estado atual no PostgreSQL e o histórico no Cassandra.
      await statusApi.atualizar(pedidoId, novoStatus, STEP_INFO[novoStatus]?.local || '')
      await carregar()
    } catch {
      setErro('Erro ao atualizar status')
    } finally {
      setAtualizando(false)
    }
  }

  if (carregando) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Carregando rastreamento...</p>
      </div>
    )
  }

  const statusAtual = pedido?.status || 'pendente'
  // Índice usado para marcar quais etapas da linha de progresso já foram concluídas.
  const stepAtual = STEPS.indexOf(statusAtual)

  return (
    <div className="container">
      <button className="btn-voltar" onClick={() => navigate('/pedidos')}>← Voltar</button>

      <div className="page-header">
        <h1>Rastrear Pedido #{pedidoId}</h1>
        {pedido && <p>{pedido.restaurante?.nome} · R$ {parseFloat(pedido.total || 0).toFixed(2)}</p>}
      </div>

      {erro && <div className="erro-msg">{erro}</div>}

      <div className="tracking-card">
        <div className="progress-tracker">
          {STEPS.map((step, index) => (
            <div key={step} className="progress-step-wrapper">
              <div className={`progress-step ${index <= stepAtual ? 'ativo' : ''}`}>
                <div className="step-circulo">
                  {STEP_INFO[step].icone}
                </div>
                <div className="step-label">{STEP_INFO[step].label}</div>
              </div>
              {index < STEPS.length - 1 && (
                <div className={`progress-linha ${index < stepAtual ? 'ativa' : ''}`} />
              )}
            </div>
          ))}
        </div>

        <div className="status-update-section">
          <h3>Atualizar Status <span className="db-badge">Cassandra</span></h3>
          <div className="status-buttons">
            {STEPS.map(step => (
              <button
                key={step}
                className={`btn-status ${statusAtual === step ? 'ativo' : ''}`}
                onClick={() => atualizarStatus(step)}
                disabled={atualizando || statusAtual === step}
              >
                {STEP_INFO[step].icone} {STEP_INFO[step].label}
              </button>
            ))}
          </div>
        </div>
      </div>

      <div className="historico-section">
        <h2>Histórico de Status <span className="db-badge">Cassandra</span></h2>
        {statusList.length === 0 ? (
          <p className="sem-dados">Sem histórico disponível (Cassandra pode não estar ativo)</p>
        ) : (
          <div className="historico-lista">
            {statusList.map((s, i) => (
              <div key={i} className="historico-item">
                <span className="historico-icone">{STEP_INFO[s.status]?.icone || '📦'}</span>
                <div className="historico-info">
                  <span className="historico-status">{STEP_INFO[s.status]?.label || s.status}</span>
                  <span className="historico-local">{s.localizacao}</span>
                </div>
                <span className="historico-hora">
                  {s.key?.timestamp ? new Date(s.key.timestamp).toLocaleString('pt-BR') : '-'}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>

      {eventos.length > 0 && (
        <div className="eventos-section">
          <h2>Eventos do Pedido <span className="db-badge">Cassandra</span></h2>
          <div className="eventos-lista">
            {eventos.map((e, i) => (
              <div key={i} className="evento-item">
                <span className="evento-tipo">{e.tipoEvento}</span>
                <span className="evento-desc">{e.descricao}</span>
                <span className="evento-hora">
                  {e.key?.horarioEvento ? new Date(e.key.horarioEvento).toLocaleString('pt-BR') : '-'}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
