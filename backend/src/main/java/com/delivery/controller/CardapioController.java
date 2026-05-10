package com.delivery.controller;

import com.delivery.model.mongo.Cardapio;
import com.delivery.service.CardapioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de cardápios (MongoDB).
 *
 * GET /api/cardapios
 *   Lista todos os cardápios de todos os restaurantes.
 *   Útil para visualizar o que está salvo no MongoDB.
 *
 * GET /api/cardapios/restaurante/{restauranteId}
 *   Busca o cardápio de um restaurante específico.
 *   Retorna HTTP 200 com o cardápio (categorias + produtos + disponibilidade)
 *   ou HTTP 404 se não houver cardápio cadastrado para aquele restaurante.
 *
 *   O frontend usa esse endpoint na tela do restaurante.
 *   Se retornar 404, o React exibe os produtos do PostgreSQL como fallback.
 */
@RestController
@RequestMapping("/api/cardapios")
@RequiredArgsConstructor
public class CardapioController {

    private final CardapioService cardapioServico;

    @GetMapping
    public ResponseEntity<List<Cardapio>> listarTodos() {
        return ResponseEntity.ok(cardapioServico.listarTodos());
    }

    @GetMapping("/restaurante/{restauranteId}")
    public ResponseEntity<?> buscarPorRestaurante(@PathVariable("restauranteId") Integer idRestaurante) {
        return cardapioServico.buscarPorRestaurante(idRestaurante)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
