package com.delivery.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * DTO que representa os dados de entrada para criar um pedido.
 *
 * Recebido via POST /api/pedidos no corpo da requisição (JSON):
 * {
 *   "usuarioId": 1,
 *   "restauranteId": 2,
 *   "itens": [
 *     { "produtoId": 3, "quantidade": 1 },
 *     { "produtoId": 4, "quantidade": 2 }
 *   ]
 * }
 *
 * O backend valida usuário e restaurante no PostgreSQL,
 * calcula o total, salva o pedido e depois grava o status inicial no Cassandra.
 */
@Data
public class PedidoRequest {
    @JsonAlias("usuarioId")
    @JsonProperty("usuarioId")
    private Integer idUsuario;

    @JsonAlias("restauranteId")
    @JsonProperty("restauranteId")
    private Integer idRestaurante;

    private List<ItemPedidoRequest> itens;
}
