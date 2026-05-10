package com.delivery.model.cassandra;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Tabela Cassandra que armazena o histórico de status de cada pedido.
 *
 * Por que Cassandra aqui?
 * Status muda frequentemente e precisamos do histórico completo com timestamp.
 * O Cassandra é excelente para séries temporais (dados ordenados por tempo)
 * e tem escrita muito rápida, ideal para atualizações constantes.
 *
 * Cada vez que o status muda (pendente → preparando → entregue), uma nova
 * linha é inserida — o Cassandra não atualiza, ele acrescenta.
 * Isso preserva o histórico completo de mudanças.
 *
 * Tabela: status_pedido(id_pedido, timestamp, status, localizacao)
 */
@Table("status_pedido")
@Data
public class StatusPedido {

    @PrimaryKey
    @JsonProperty("key")
    private StatusPedidoKey chave; // contem id_pedido + timestamp

    @Column("status")
    private String status; // "pendente", "preparando" ou "entregue"

    @Column("localizacao")
    private String localizacao; // ex: "Restaurante", "Em trânsito", "Entregue ao cliente"
}
