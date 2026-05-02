package com.delivery.repository.postgres;

import com.delivery.model.postgres.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório JPA para a entidade Pedido (PostgreSQL).
 *
 * Método customizado gerado automaticamente pelo Spring Data:
 *
 *   findByUsuarioIdOrderByDataHoraDesc(usuarioId)
 *   → SELECT * FROM pedidos WHERE usuario_id = ? ORDER BY data_hora DESC
 *   Retorna os pedidos do usuário do mais recente para o mais antigo.
 *   Usado na tela "Meus Pedidos".
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    @Query("select pedido from Pedido pedido where pedido.usuario.id = :idUsuario order by pedido.dataHora desc")
    List<Pedido> buscarPorIdUsuarioOrdenadoPorDataHoraDesc(@Param("idUsuario") Integer idUsuario);
}
