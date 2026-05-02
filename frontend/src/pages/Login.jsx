/**
 * Tela de Login.
 *
 * Fluxo:
 *   1. Usuário preenche email e senha
 *   2. handleSubmit() chama POST /api/auth/login
 *   3. Backend valida no PostgreSQL (tabela usuarios)
 *   4. Sucesso → login() salva os dados no contexto + localStorage → redireciona para Home
 *   5. Erro    → exibe a mensagem de erro do backend (ex: "Email ou senha incorretos")
 *
 * O estado de carregamento desabilita o botão para evitar cliques duplos.
 * Usuários de teste (já no banco): gustavo1@email.com / 123
 */

import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authApi } from '../services/api'

export default function Login() {
  const [form, setForm] = useState({ email: '', senha: '' })
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)
  const { login } = useAuth()

  async function handleSubmit(e) {
    e.preventDefault()  // evita recarregar a página
    setErro('')
    setCarregando(true)
    try {
      const res = await authApi.login(form)
      login(res.data) // salva no contexto → App.jsx redireciona para /
    } catch (err) {
      setErro(err.response?.data?.erro || 'Erro ao fazer login')
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-logo">🍕</div>
        <h1>DeliveryApp</h1>
        <h2>Entrar na conta</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              value={form.email}
              onChange={e => setForm({ ...form, email: e.target.value })}
              placeholder="seu@email.com"
              required
            />
          </div>
          <div className="form-group">
            <label>Senha</label>
            <input
              type="password"
              value={form.senha}
              onChange={e => setForm({ ...form, senha: e.target.value })}
              placeholder="sua senha"
              required
            />
          </div>
          {erro && <p className="erro-msg">{erro}</p>}
          <button type="submit" className="btn-primary" disabled={carregando}>
            {carregando ? 'Entrando...' : 'Entrar'}
          </button>
        </form>
        <p className="auth-link">
          Não tem conta? <Link to="/cadastro">Cadastre-se</Link>
        </p>
      </div>
    </div>
  )
}
