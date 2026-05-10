package com.delivery.repository.cassandra;

import com.delivery.model.cassandra.LogSistema;
import com.delivery.model.cassandra.LogSistemaKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório Cassandra para a tabela logs_sistema.
 *
 * findByKeyNomeServico(nomeServico)
 * → SELECT * FROM logs_sistema WHERE nome_servico = ?
 * Retorna todos os logs de um serviço específico (ex: "PedidoService").
 *
 * Acessível via GET /api/status/logs?servico=PedidoService
 * Útil para visualizar o que o sistema fez, útil para depuração.
 */
@Repository
public interface LogSistemaRepository extends CassandraRepository<LogSistema, LogSistemaKey> {
    @Query("SELECT * FROM logs_sistema WHERE nome_servico = ?0")
    List<LogSistema> buscarPorNomeServico(String nomeServico);
}
