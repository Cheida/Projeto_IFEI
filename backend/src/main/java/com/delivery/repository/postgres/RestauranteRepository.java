package com.delivery.repository.postgres;

import com.delivery.model.postgres.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório JPA para a entidade Restaurante (PostgreSQL).
 *
 * Apenas herda os métodos padrão do JpaRepository:
 *   findAll()      → lista todos os restaurantes (usado na tela Home)
 *   findById(id)   → busca um restaurante pelo ID (usado na tela de cardápio)
 *   save(r)        → salva ou atualiza um restaurante
 *
 * Não precisamos de métodos customizados aqui pois as buscas são simples.
 */
@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> {
}
