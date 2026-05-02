package com.delivery.repository.postgres;

import com.delivery.model.postgres.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório JPA para a entidade Produto (PostgreSQL).
 *
 * O método customizado abaixo é gerado automaticamente pelo Spring Data
 * a partir do nome do método (query derivada):
 *
 *   findByRestauranteId(id) → SELECT * FROM produtos WHERE restaurante_id = ?
 *   Usado para carregar os produtos de um restaurante específico.
 *   Se o MongoDB não tiver o cardápio, o frontend usa esses produtos.
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    @Query("select produto from Produto produto where produto.restaurante.id = :idRestaurante")
    List<Produto> buscarPorIdRestaurante(@Param("idRestaurante") Integer idRestaurante);
}
