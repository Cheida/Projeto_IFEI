/**
 * Tela do restaurante e montagem do pedido.
 *
 * Fluxo:
 *   1. Lê o id do restaurante pela URL (/restaurante/:id)
 *   2. Busca dados do restaurante no PostgreSQL
 *   3. Tenta buscar o cardápio detalhado no MongoDB
 *   4. Busca também os produtos do PostgreSQL, usados para criar itens de pedido válidos
 *   5. O usuário adiciona/remove produtos no carrinho
 *   6. Ao finalizar, envia POST /api/pedidos para salvar pedido e itens
 *
 * Quando o MongoDB não responde, a tela ainda funciona usando a lista simples do PostgreSQL.
 */

import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { restauranteApi, pedidoApi } from '../services/api'
import { useAuth } from '../context/AuthContext'

export default function Restaurante() {
  const { id } = useParams()
  const { user } = useAuth()
  const navigate = useNavigate()

  const [restaurante, setRestaurante] = useState(null)
  const [cardapio, setCardapio] = useState(null)
  const [produtos, setProdutos] = useState([])
  const [carrinho, setCarrinho] = useState([])
  const [carregando, setCarregando] = useState(true)
  const [pedindo, setPedindo] = useState(false)
  const [mensagem, setMensagem] = useState('')

  useEffect(() => {
    // Carrega tudo em paralelo. O cardápio MongoDB é opcional: se falhar, vira null.
    Promise.all([
      restauranteApi.buscar(id),
      restauranteApi.cardapio(id).catch(() => ({ data: null })),
      restauranteApi.produtos(id),
    ]).then(([r, c, p]) => {
      setRestaurante(r.data)
      setCardapio(c.data)
      setProdutos(p.data)
    }).finally(() => setCarregando(false))
  }, [id])

  function adicionarItem(produto) {
    setCarrinho(prev => {
      // Se o produto já estiver no carrinho, apenas aumenta a quantidade.
      const existente = prev.find(i => i.produtoId === produto.id)
      if (existente) {
        return prev.map(i => i.produtoId === produto.id
          ? { ...i, quantidade: i.quantidade + 1 }
          : i)
      }
      return [...prev, { produtoId: produto.id, nome: produto.nome, preco: produto.preco, quantidade: 1 }]
    })
  }

  function removerItem(produtoId) {
    setCarrinho(prev => {
      const item = prev.find(i => i.produtoId === produtoId)
      if (!item) return prev
      // Quando chega em 1 unidade, o próximo clique remove o item do carrinho.
      if (item.quantidade === 1) return prev.filter(i => i.produtoId !== produtoId)
      return prev.map(i => i.produtoId === produtoId ? { ...i, quantidade: i.quantidade - 1 } : i)
    })
  }

  const total = carrinho.reduce((acc, i) => acc + i.preco * i.quantidade, 0)

  async function realizarPedido() {
    if (carrinho.length === 0) return
    setPedindo(true)
    setMensagem('')
    try {
      // O backend calcula e persiste o pedido no PostgreSQL e registra histórico no Cassandra.
      const res = await pedidoApi.criar({
        usuarioId: user.id,
        restauranteId: parseInt(id),
        itens: carrinho.map(i => ({ produtoId: i.produtoId, quantidade: i.quantidade })),
      })
      setMensagem(`✅ Pedido #${res.data.id} realizado! Redirecionando...`)
      setCarrinho([])
      setTimeout(() => navigate('/pedidos'), 2000)
    } catch (err) {
      setMensagem(`❌ ${err.response?.data?.erro || 'Erro ao realizar pedido'}`)
    } finally {
      setPedindo(false)
    }
  }

  if (carregando) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Carregando cardápio...</p>
      </div>
    )
  }

  return (
    <div className="container">
      <button className="btn-voltar" onClick={() => navigate('/')}>← Voltar</button>

      <div className="restaurante-header">
        <h1>{restaurante?.nome}</h1>
        <p>{restaurante?.endereco}</p>
        {cardapio && <span className="badge-db">Cardápio via MongoDB</span>}
        {!cardapio && <span className="badge-db badge-pg">Produtos via PostgreSQL</span>}
      </div>

      {mensagem && <div className={mensagem.startsWith('✅') ? 'success-msg' : 'erro-msg'}>{mensagem}</div>}

      <div className="restaurante-layout">
        <div className="menu-section">
          <h2>Cardápio</h2>

          {cardapio ? (
            cardapio.categorias?.map(categoria => (
              <div key={categoria.nome} className="categoria-section">
                <h3 className="categoria-titulo">{categoria.nome}</h3>
                <div className="produtos-grid">
                  {categoria.produtos.map(prod => {
                    // Liga o produto do MongoDB ao produto relacional pelo nome para obter o id do PostgreSQL.
                    const pgProduto = produtos.find(p => p.nome === prod.nome)
                    const itemNoCarrinho = carrinho.find(i => i.produtoId === pgProduto?.id)
                    return (
                      <div key={prod.nome} className={`produto-card ${!prod.disponivel ? 'indisponivel' : ''}`}>
                        <h4>{prod.nome}</h4>
                        <p className="produto-preco">R$ {prod.precoBase?.toFixed(2)}</p>
                        <span className={`disponivel-tag ${prod.disponivel ? 'sim' : 'nao'}`}>
                          {prod.disponivel ? 'Disponível' : 'Indisponível'}
                        </span>
                        {prod.disponivel && pgProduto && (
                          <div className="produto-acoes">
                            {itemNoCarrinho ? (
                              <div className="qty-controls">
                                <button onClick={() => removerItem(pgProduto.id)}>−</button>
                                <span>{itemNoCarrinho.quantidade}</span>
                                <button onClick={() => adicionarItem({ id: pgProduto.id, nome: prod.nome, preco: prod.precoBase })}>+</button>
                              </div>
                            ) : (
                              <button className="btn-add" onClick={() => adicionarItem({ id: pgProduto.id, nome: prod.nome, preco: prod.precoBase })}>
                                + Adicionar
                              </button>
                            )}
                          </div>
                        )}
                      </div>
                    )
                  })}
                </div>
              </div>
            ))
          ) : (
            <div className="produtos-grid">
              {produtos.map(produto => {
                const itemNoCarrinho = carrinho.find(i => i.produtoId === produto.id)
                return (
                  <div key={produto.id} className="produto-card">
                    <h4>{produto.nome}</h4>
                    <p className="produto-preco">R$ {parseFloat(produto.preco).toFixed(2)}</p>
                    <div className="produto-acoes">
                      {itemNoCarrinho ? (
                        <div className="qty-controls">
                          <button onClick={() => removerItem(produto.id)}>−</button>
                          <span>{itemNoCarrinho.quantidade}</span>
                          <button onClick={() => adicionarItem({ id: produto.id, nome: produto.nome, preco: parseFloat(produto.preco) })}>+</button>
                        </div>
                      ) : (
                        <button className="btn-add" onClick={() => adicionarItem({ id: produto.id, nome: produto.nome, preco: parseFloat(produto.preco) })}>
                          + Adicionar
                        </button>
                      )}
                    </div>
                  </div>
                )
              })}
            </div>
          )}
        </div>

        <div className="carrinho-section">
          <h2>Carrinho</h2>
          {carrinho.length === 0 ? (
            <p className="carrinho-vazio">Nenhum item adicionado</p>
          ) : (
            <>
              <div className="carrinho-itens">
                {carrinho.map(item => (
                  <div key={item.produtoId} className="carrinho-item">
                    <div className="carrinho-item-info">
                      <span className="carrinho-item-nome">{item.nome}</span>
                      <span className="carrinho-item-qtd">x{item.quantidade}</span>
                    </div>
                    <span className="carrinho-item-preco">R$ {(item.preco * item.quantidade).toFixed(2)}</span>
                  </div>
                ))}
              </div>
              <div className="carrinho-total">
                <strong>Total: R$ {total.toFixed(2)}</strong>
              </div>
              <button
                className="btn-primary btn-pedido"
                onClick={realizarPedido}
                disabled={pedindo}
              >
                {pedindo ? 'Processando...' : 'Fazer Pedido'}
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  )
}
