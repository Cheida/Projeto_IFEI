package com.delivery.repository.mongo;

import com.delivery.model.mongo.Cardapio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório MongoDB para o documento Cardapio.
 *
 * Estende MongoRepository (equivalente ao JpaRepository, mas para MongoDB).
 * O Spring Data gera a query automaticamente a partir do nome do método:
 *
 *   findByRestauranteId(id)
 *   → db.cardapios.findOne({ restaurante_id: id })
 *   Usado para carregar o cardápio de um restaurante na tela de pedido.
 *
 * O Optional<> indica que pode não existir cardápio para aquele restaurante.
 * Nesse caso, o frontend exibe os produtos do PostgreSQL como fallback.
 */
@Repository
public interface CardapioRepository extends MongoRepository<Cardapio, String> {
    @Query("{ 'restaurante_id' : ?0 }")
    Optional<Cardapio> buscarPorIdRestaurante(Integer idRestaurante);
}
