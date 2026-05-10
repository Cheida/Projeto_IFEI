package com.delivery.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO que representa um item dentro de um pedido (produto + quantidade).
 *
 * Parte do PedidoRequest. Exemplo de uso dentro do JSON de pedido:
 * { "produtoId": 1, "quantidade": 2 }
 *
 * O backend usa o produtoId para buscar o produto no PostgreSQL
 * e calcular o preço (produtoId × quantidade = subtotal).
 */
@Data
public class ItemPedidoRequest {
    @JsonAlias("produtoId")
    @JsonProperty("produtoId")
    private Integer idProduto;

    private Integer quantidade;
}
