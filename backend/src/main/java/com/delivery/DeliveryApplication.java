package com.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada do backend.
 *
 * @SpringBootApplication ativa a autoconfiguração do Spring Boot,
 * que detecta automaticamente as configurações de JPA (PostgreSQL),
 * MongoDB e Cassandra definidas em application.properties.
 *
 * Para rodar:
 *   Linux/Mac → ./mvnw spring-boot:run
 *   Windows   → mvnw.cmd spring-boot:run
 *
 * O servidor sobe na porta 8080 por padrão.
 */
@SpringBootApplication
public class DeliveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryApplication.class, args);
    }
}
