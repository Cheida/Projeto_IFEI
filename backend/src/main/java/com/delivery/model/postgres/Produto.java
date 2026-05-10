package com.delivery.model.postgres;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade mapeada para a tabela "produtos" no PostgreSQL (Supabase).
 *
 * Cada produto pertence a um restaurante (relacionamento ManyToOne).
 *
 * Por que FetchType.LAZY?
 * Evita carregar o restaurante do banco toda vez que um produto é buscado.
 * O restaurante só é carregado quando realmente necessário.
 *
 * @JsonIgnoreProperties evita que o serializer JSON quebre quando o campo
 * "restaurante" ainda não foi carregado pelo Hibernate (lazy loading proxy).
 *
 * Tabela: produtos(id, nome, preco, restaurante_id, data_hora)
 */
@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Restaurante restaurante;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;
}
