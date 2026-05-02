// Componente raiz da aplicação React.
// Define as rotas, o contexto de autenticação e a proteção de rotas privadas.

import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Navbar from './components/Navbar'
import Login from './pages/Login'
import Cadastro from './pages/Cadastro'
import Home from './pages/Home'
import Restaurante from './pages/Restaurante'
import MeusPedidos from './pages/MeusPedidos'
import Tracking from './pages/Tracking'

/**
 * Wrapper que protege rotas privadas.
 * Se o usuário não estiver logado, redireciona para /login.
 */
function PrivateRoute({ children }) {
  const { user } = useAuth()
  return user ? children : <Navigate to="/login" replace />
}

/**
 * Define todas as rotas da aplicação.
 * A Navbar só aparece quando o usuário está logado.
 *
 * Rotas:
 *   /login       → tela de login (pública)
 *   /cadastro    → tela de cadastro (pública)
 *   /            → lista de restaurantes (privada)
 *   /restaurante/:id → cardápio do restaurante (privada)
 *   /pedidos     → meus pedidos (privada)
 *   /tracking/:pedidoId → rastreamento do pedido (privada)
 */
function AppRoutes() {
  const { user } = useAuth()
  return (
    <>
      {user && <Navbar />}
      <Routes>
        <Route path="/login"    element={!user ? <Login />    : <Navigate to="/" replace />} />
        <Route path="/cadastro" element={!user ? <Cadastro /> : <Navigate to="/" replace />} />
        <Route path="/"                    element={<PrivateRoute><Home /></PrivateRoute>} />
        <Route path="/restaurante/:id"     element={<PrivateRoute><Restaurante /></PrivateRoute>} />
        <Route path="/pedidos"             element={<PrivateRoute><MeusPedidos /></PrivateRoute>} />
        <Route path="/tracking/:pedidoId"  element={<PrivateRoute><Tracking /></PrivateRoute>} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  )
}

// AuthProvider envolve tudo para que qualquer componente acesse o usuário logado
export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <AppRoutes />
      </BrowserRouter>
    </AuthProvider>
  )
}
