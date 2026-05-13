/**
 * Tela de Cadastro.
 *
 * Fluxo:
 *   1. Usuário preenche nome, email e senha
 *   2. handleSubmit() chama POST /api/auth/cadastro
 *   3. Backend salva no PostgreSQL (tabela usuarios)
 *   4. Sucesso → login() automático → redireciona para Home
 *   5. Erro    → exibe mensagem (ex: "Email já cadastrado")
 *
 * Após o cadastro, o usuário já fica logado automaticamente.
 */

import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { authApi } from '../services/api'

export default function Cadastro() {
  const [form, setForm] = useState({ nome: '', email: '', senha: '' })
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)
  const { login } = useAuth()

  async function handleSubmit(e) {
    e.preventDefault()
    setErro('')
    setCarregando(true)
    try {
      const res = await authApi.cadastro(form)
      login(res.data) // loga automaticamente após cadastro
    } catch (err) {
      setErro(err.response?.data?.erro || 'Erro ao cadastrar')
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-logo">🍕</div>
        <h1>Ifei</h1>
        <h2>Criar conta</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Nome</label>
            <input
              type="text"
              value={form.nome}
              onChange={e => setForm({ ...form, nome: e.target.value })}
              placeholder="Seu nome completo"
              required
            />
          </div>
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
              placeholder="Mínimo 3 caracteres"
              required
            />
          </div>
          {erro && <p className="erro-msg">{erro}</p>}
          <button type="submit" className="btn-primary" disabled={carregando}>
            {carregando ? 'Criando conta...' : 'Criar conta'}
          </button>
        </form>
        <p className="auth-link">
          Já tem conta? <Link to="/login">Entrar</Link>
        </p>
      </div>
    </div>
  )
}
