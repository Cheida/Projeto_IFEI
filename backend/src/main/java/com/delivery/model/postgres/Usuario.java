package com.delivery.model.postgres;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade mapeada para a tabela "usuarios" no PostgreSQL (Supabase).
 *
 * @Data        → Lombok gera getters, setters, toString, equals e hashCode
 * @NoArgsConstructor / @AllArgsConstructor → construtores gerados pelo Lombok
 * @Entity      → informa ao JPA (Hibernate) que essa classe é uma tabela
 * @JsonIgnore  → o campo "senha" nunca aparece nas respostas da API (segurança)
 *
 * Tabela: usuarios(id, nome, email, senha, data_hora)
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incremento do PostgreSQL
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @JsonIgnore // nunca retorna a senha na API
    @Column(nullable = false, length = 100)
    private String senha;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;
}
