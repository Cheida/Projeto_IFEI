package com.delivery.repository.postgres;

import com.delivery.model.postgres.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA para a entidade Usuario (PostgreSQL).
 *
 * Estende JpaRepository, que já fornece os métodos básicos:
 *   save(), findById(), findAll(), delete(), etc.
 *
 * Os métodos abaixo são gerados automaticamente pelo Spring Data
 * a partir do nome do método (query derivada):
 *   - findByEmail → SELECT * FROM usuarios WHERE email = ?
 *   - findByEmailAndSenha → SELECT * FROM usuarios WHERE email = ? AND senha = ?
 *     (usado no login; em produção real a senha deveria ser hasheada com BCrypt)
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    @Query("select usuario from Usuario usuario where usuario.email = :email")
    Optional<Usuario> buscarPorEmail(@Param("email") String email);

    @Query("select usuario from Usuario usuario where usuario.email = :email and usuario.senha = :senha")
    Optional<Usuario> buscarPorEmailESenha(@Param("email") String email, @Param("senha") String senha);
}
