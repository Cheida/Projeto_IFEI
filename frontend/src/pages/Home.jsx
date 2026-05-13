/**
 * Tela inicial após o login.
 *
 * Fluxo:
 *   1. Ao abrir a tela, busca os restaurantes no PostgreSQL via GET /api/restaurantes
 *   2. Enquanto a API responde, mostra o loading
 *   3. Se a busca falhar, exibe uma mensagem orientando a verificar o backend
 *   4. Ao clicar em um restaurante, navega para /restaurante/:id
 *
 * Esta tela é o ponto de entrada para o usuário escolher onde vai fazer o pedido.
 */

import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { restauranteApi } from '../services/api'

const ICONES = ['🍔', '🍕', '🍣', '🥩', '🌮', '🍱', '🥗', '🌱', '🥟', '🍬']

export default function Home() {
  const [restaurantes, setRestaurantes] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [erro, setErro] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    // Lista os restaurantes cadastrados no PostgreSQL/Supabase.
    restauranteApi.listar()
      .then(res => setRestaurantes(res.data))
      .catch(() => setErro('Erro ao carregar restaurantes. Verifique se o backend está rodando.'))
      .finally(() => setCarregando(false))
  }, [])

  if (carregando) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Carregando restaurantes...</p>
      </div>
    )
  }

  return (
    <div className="container">
      <div className="page-header">
        <h1>Restaurantes</h1>
        <p>Escolha onde quer pedir</p>
      </div>

      {erro && <div className="erro-msg">{erro}</div>}

      <div className="restaurantes-grid">
        {restaurantes.map((r, i) => (
          <div
            key={r.id}
            className="restaurante-card"
            onClick={() => navigate(`/restaurante/${r.id}`)}
          >
            <div className="restaurante-icone">{ICONES[i % ICONES.length]}</div>
            <div className="restaurante-info">
              <h3>{r.nome}</h3>
              <p className="restaurante-endereco">{r.endereco}</p>
            </div>
            <button className="btn-ver-cardapio">Ver cardápio →</button>
          </div>
        ))}
      </div>
    </div>
  )
}
