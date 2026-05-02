package com.delivery.model.cassandra;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Tabela Cassandra que registra eventos ocorridos em um pedido.
 *
 * Diferença entre evento e status:
 *   - status_pedido → onde o pedido está AGORA (estado atual com localização)
 *   - eventos_pedido → o que ACONTECEU (log de ações com descrição)
 *
 * Exemplos de eventos gravados automaticamente:
 *   PEDIDO_CRIADO    → quando o usuário finaliza o pedido no frontend
 *   STATUS_ATUALIZADO → cada vez que o status muda
 *
 * Tabela: eventos_pedido(id_pedido, horario_evento, tipo_evento, descricao)
 */
@Table("eventos_pedido")
@Data
public class EventoPedido {

    @PrimaryKey
    @JsonProperty("key")
    private EventoPedidoKey chave; // id_pedido + horario_evento

    @Column("tipo_evento")
    private String tipoEvento; // ex: "PEDIDO_CRIADO", "STATUS_ATUALIZADO"

    @Column("descricao")
    private String descricao; // ex: "Pedido criado com 2 item(ns). Total: R$ 40.00"
}
