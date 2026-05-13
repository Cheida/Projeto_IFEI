import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Configuração do Vite para desenvolvimento local.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        // Encaminha chamadas /api do frontend para o backend Spring Boot.
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
