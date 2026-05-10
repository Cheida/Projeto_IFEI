package com.delivery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração de CORS (Cross-Origin Resource Sharing).
 *
 * Sem isso, o navegador bloqueia as requisições do frontend (porta 5173)
 * para o backend (porta 8080), pois são origens diferentes.
 * Aqui liberamos explicitamente as origens do frontend de desenvolvimento.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")                          // aplica a todas as rotas /api/...
                .allowedOrigins("http://localhost:5173",        // Vite (React dev server)
                                "http://localhost:3000")        // Create React App (alternativa)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
