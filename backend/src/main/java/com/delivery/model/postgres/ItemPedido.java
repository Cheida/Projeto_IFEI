package com.delivery.model.postgres;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidade mapeada para a tabela "itens_pedido" no PostgreSQL (Supabase).
 *
 * É a tabela de ligação entre pedidos e produtos (relação N:N).
 * Cada linha responde: "o pedido X contém Y unidades do produto Z".
 *
 * O @JsonIgnoreProperties no campo "pedido" evita referência circular
 * na serialização JSON (pedido → item → pedido → item → infinito).
 *
 * Tabela: itens_pedido(id, pedido_id, produto_id, quantidade)
 */
@Entity
@Table(name = "itens_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ignora os campos de Pedido que causariam loop na serialização JSON
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "usuario", "restaurante", "itens"})
    private Pedido pedido;

    // carrega o produto junto (EAGER) para a API retornar nome e preço
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "restaurante"})
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;
}
