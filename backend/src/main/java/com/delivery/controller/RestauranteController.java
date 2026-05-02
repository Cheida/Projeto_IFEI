package com.delivery.controller;

import com.delivery.model.postgres.Produto;
import com.delivery.model.postgres.Restaurante;
import com.delivery.service.RestauranteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de restaurantes e produtos (PostgreSQL).
 *
 * GET /api/restaurantes
 *   Lista todos os restaurantes. Usado na tela Home do frontend.
 *
 * GET /api/restaurantes/{id}
 *   Busca um restaurante pelo ID. Usado no cabeçalho da página do restaurante.
 *
 * GET /api/restaurantes/{id}/produtos
 *   Lista os produtos de um restaurante (dados do PostgreSQL).
 *   O frontend chama isso sempre, mas só exibe se não houver cardápio no MongoDB.
 *   Se o MongoDB tiver o cardápio, os dados do MongoDB têm prioridade
 *   (mais ricos: categorias, disponibilidade).
 */
@RestController
@RequestMapping("/api/restaurantes")
@RequiredArgsConstructor
public class RestauranteController {

    private final RestauranteService restauranteServico;

    @GetMapping
    public ResponseEntity<List<Restaurante>> listarTodos() {
        return ResponseEntity.ok(restauranteServico.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable("id") Integer idRestaurante) {
        try {
            return ResponseEntity.ok(restauranteServico.buscarPorId(idRestaurante));
        } catch (Exception excecao) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/produtos")
    public ResponseEntity<List<Produto>> listarProdutos(@PathVariable("id") Integer idRestaurante) {
        return ResponseEntity.ok(restauranteServico.listarProdutos(idRestaurante));
    }
}
