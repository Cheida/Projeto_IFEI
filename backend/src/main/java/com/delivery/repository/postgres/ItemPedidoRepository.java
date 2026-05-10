package com.delivery.repository.postgres;

import com.delivery.model.postgres.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório JPA para a entidade ItemPedido (PostgreSQL).
 *
 * Método customizado gerado automaticamente pelo Spring Data:
 *
 *   findByPedidoId(pedidoId)
 *   → SELECT * FROM itens_pedido WHERE pedido_id = ?
 *   Retorna todos os itens (produtos + quantidade) de um pedido específico.
 *   Usado no endpoint GET /api/pedidos/{id}/itens.
 */
@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Integer> {
    @Query("select itemPedido from ItemPedido itemPedido where itemPedido.pedido.id = :idPedido")
    List<ItemPedido> buscarPorIdPedido(@Param("idPedido") Integer idPedido);
}
