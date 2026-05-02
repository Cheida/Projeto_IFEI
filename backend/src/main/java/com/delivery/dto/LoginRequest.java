package com.delivery.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) que representa os dados de entrada do login.
 *
 * DTOs são objetos simples usados apenas para transportar dados entre
 * o frontend e o backend — não são entidades do banco de dados.
 *
 * Recebido via POST /api/auth/login no corpo da requisição (JSON):
 * { "email": "gustavo@email.com", "senha": "123" }
 */
@Data
public class LoginRequest {
    private String email;
    private String senha;
}
