package com.delivery.model.cassandra;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Chave primária composta da tabela "eventos_pedido" no Cassandra.
 *
 * Mesma estrutura do StatusPedidoKey:
 *   - id_pedido (partition key): todos os eventos do pedido ficam juntos
 *   - horario_evento (clustering): ordenação decrescente (evento mais recente primeiro)
 *
 * CQL equivalente:
 *   PRIMARY KEY (id_pedido, horario_evento) WITH CLUSTERING ORDER BY (horario_evento DESC)
 */
@PrimaryKeyClass
@Data
public class EventoPedidoKey implements Serializable {

    @PrimaryKeyColumn(name = "id_pedido", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID idPedido;

    @PrimaryKeyColumn(name = "horario_evento", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant horarioEvento;
}
