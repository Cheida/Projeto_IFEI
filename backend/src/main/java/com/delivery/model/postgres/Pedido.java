package com.delivery.model.postgres;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade mapeada para a tabela "pedidos" no PostgreSQL (Supabase).
 *
 * Um pedido pertence a um usuário e a um restaurante (dois ManyToOne).
 *
 * O campo "status" aceita apenas: 'pendente', 'preparando' ou 'entregue'
 * (restrição CHECK definida no SQL do banco).
 *
 * Quando o status muda, ele é atualizado aqui (PostgreSQL) E também gravado
 * no Cassandra (tabela status_pedido) para manter o histórico com timestamp.
 *
 * Tabela: pedidos(id, usuario_id, restaurante_id, total, status, data_hora)
 */
@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // FetchType.EAGER carrega o usuário junto com o pedido (necessário para a resposta da API)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurante_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Restaurante restaurante;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    // valores possíveis: 'pendente', 'preparando', 'entregue'
    @Column(length = 50)
    private String status;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;
}
