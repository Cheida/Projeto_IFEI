package com.delivery.model.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade mapeada para a tabela "restaurantes" no PostgreSQL (Supabase).
 *
 * Os 10 restaurantes já foram inseridos pelo script SQL do projeto
 * (Burger House, Pizza Mania, Sushi Top, etc.).
 *
 * Tabela: restaurantes(id, nome, endereco, data_hora)
 */
@Entity
@Table(name = "restaurantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private String endereco;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;
}
