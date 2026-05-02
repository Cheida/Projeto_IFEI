/**
 * Barra de navegação superior — exibida apenas quando o usuário está logado.
 *
 * Exibe:
 *   - Logo/nome do app (link para a Home)
 *   - Link para "Restaurantes" (Home)
 *   - Link para "Meus Pedidos"
 *   - Nome do usuário logado
 *   - Botão "Sair" (chama logout() do contexto e redireciona para /login)
 *
 * O componente só é renderizado no App.jsx quando { user } existe no AuthContext.
 */

import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()           // limpa o estado e o localStorage
    navigate('/login') // redireciona para a tela de login
  }

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/">DeliveryApp</Link>
      </div>
      <div className="navbar-menu">
        <Link to="/" className="nav-link">Restaurantes</Link>
        <Link to="/pedidos" className="nav-link">Meus Pedidos</Link>
        <span className="navbar-user">Olá, {user?.nome}</span>
        <button onClick={handleLogout} className="btn-logout">Sair</button>
      </div>
    </nav>
  )
}
