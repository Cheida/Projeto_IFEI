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
 * Chave primária composta da tabela "status_pedido" no Cassandra.
 *
 * No Cassandra, a PRIMARY KEY é dividida em:
 *   - Partition Key (id_pedido): define em qual nó do cluster os dados ficam.
 *     Todos os status do mesmo pedido ficam juntos — busca rápida.
 *   - Clustering Column (timestamp): ordena os registros dentro da partição.
 *     DESCENDING = status mais recente vem primeiro na consulta.
 *
 * A classe precisa implementar Serializable e ter equals/hashCode
 * corretos (o @Data do Lombok resolve isso automaticamente).
 *
 * CQL equivalente:
 *   PRIMARY KEY (id_pedido, timestamp) WITH CLUSTERING ORDER BY (timestamp DESC)
 */
@PrimaryKeyClass
@Data
public class StatusPedidoKey implements Serializable {

    // partition key: agrupa todos os status do mesmo pedido no mesmo nó
    @PrimaryKeyColumn(name = "id_pedido", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID idPedido;

    // clustering column: ordena do mais recente para o mais antigo
    @PrimaryKeyColumn(name = "timestamp", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant timestamp;
}
