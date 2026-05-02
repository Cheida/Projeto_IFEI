package com.delivery.model.cassandra;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;

/**
 * Chave primária composta da tabela "logs_sistema" no Cassandra.
 *
 * Aqui a partition key é o nome do serviço (ex: "PedidoService", "StatusService").
 * Isso permite buscar todos os logs de um serviço específico de forma eficiente,
 * pois todos ficam na mesma partição do Cassandra.
 *
 * horario_log (clustering, DESCENDING): log mais recente vem primeiro.
 *
 * CQL equivalente:
 *   PRIMARY KEY (nome_servico, horario_log) WITH CLUSTERING ORDER BY (horario_log DESC)
 */
@PrimaryKeyClass
@Data
public class LogSistemaKey implements Serializable {

    @PrimaryKeyColumn(name = "nome_servico", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String nomeServico; // ex: "PedidoService", "StatusService"

    @PrimaryKeyColumn(name = "horario_log", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant horarioLog;
}
