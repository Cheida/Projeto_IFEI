/**
 * Contexto de autenticação da aplicação.
 *
 * Usa a Context API do React para compartilhar o estado do usuário logado
 * com qualquer componente da árvore, sem precisar passar props manualmente.
 *
 * O estado persiste no localStorage:
 *   - Ao fazer login, os dados do usuário são salvos em localStorage['usuario']
 *   - Ao recarregar a página, o estado é restaurado automaticamente
 *   - Ao fazer logout, o localStorage é limpo
 *
 * useAuth() → hook customizado para acessar { user, login, logout } em qualquer componente
 */

import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  // inicializa a partir do localStorage (mantém login após recarregar a página)
  const [user, setUser] = useState(() => {
    try {
      const saved = localStorage.getItem('usuario')
      return saved ? JSON.parse(saved) : null
    } catch {
      return null // em caso de JSON inválido no localStorage
    }
  })

  /** Chamado após login ou cadastro bem-sucedido */
  function login(userData) {
    setUser(userData)
    localStorage.setItem('usuario', JSON.stringify(userData))
  }

  /** Limpa o estado e redireciona para /login (redirecionamento feito no componente) */
  function logout() {
    setUser(null)
    localStorage.removeItem('usuario')
  }

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

/** Hook para consumir o contexto de autenticação */
export function useAuth() {
  return useContext(AuthContext)
}
