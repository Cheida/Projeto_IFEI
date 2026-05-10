package com.delivery.repository.cassandra;

import com.delivery.model.cassandra.StatusPedido;
import com.delivery.model.cassandra.StatusPedidoKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositório Cassandra para a tabela status_pedido.
 *
 * Estende CassandraRepository (equivalente ao JpaRepository para Cassandra).
 *
 * findByKeyIdPedido(uuid)
 * → SELECT * FROM status_pedido WHERE id_pedido = ?
 * Retorna todo o histórico de status de um pedido, ordenado do mais recente
 * para o mais antigo (definido pelo CLUSTERING ORDER BY da tabela).
 *
 * O UUID é derivado do ID inteiro do pedido via UUID.nameUUIDFromBytes()
 * para manter consistência entre PostgreSQL (int) e Cassandra (UUID).
 */
@Repository
public interface StatusPedidoRepository extends CassandraRepository<StatusPedido, StatusPedidoKey> {
    @Query("SELECT * FROM status_pedido WHERE id_pedido = ?0")
    List<StatusPedido> buscarPorIdPedido(UUID idPedido);
}
