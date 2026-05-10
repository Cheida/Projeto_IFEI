package com.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO que representa a resposta do login ou cadastro.
 *
 * Retornado como JSON para o frontend após login bem-sucedido:
 * { "id": 1, "nome": "Gustavo", "email": "gustavo@email.com", "mensagem": "Login realizado com sucesso" }
 *
 * O frontend salva esses dados no localStorage para identificar o usuário logado.
 * Note que a senha NÃO está incluída aqui (nem na entidade Usuario via @JsonIgnore).
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private Integer id;
    private String nome;
    private String email;
    private String mensagem;
}
