package com.delivery.repository.cassandra;

import com.delivery.model.cassandra.EventoPedido;
import com.delivery.model.cassandra.EventoPedidoKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositório Cassandra para a tabela eventos_pedido.
 *
 * findByKeyIdPedido(uuid)
 * → SELECT * FROM eventos_pedido WHERE id_pedido = ?
 * Retorna todos os eventos de um pedido (PEDIDO_CRIADO, STATUS_ATUALIZADO...),
 * ordenados do mais recente para o mais antigo.
 *
 * Usado na tela de rastreamento para mostrar o histórico de ações do pedido.
 */
@Repository
public interface EventoPedidoRepository extends CassandraRepository<EventoPedido, EventoPedidoKey> {
    @Query("SELECT * FROM eventos_pedido WHERE id_pedido = ?0")
    List<EventoPedido> buscarPorIdPedido(UUID idPedido);
}
