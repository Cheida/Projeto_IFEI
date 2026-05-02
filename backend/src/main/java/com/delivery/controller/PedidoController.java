package com.delivery.controller;

import com.delivery.dto.PedidoRequest;
import com.delivery.model.postgres.ItemPedido;
import com.delivery.model.postgres.Pedido;
import com.delivery.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller de pedidos — integra PostgreSQL e Cassandra.
 *
 * POST /api/pedidos
 *   Recebe os dados do pedido (usuário, restaurante, itens).
 *   Salva no PostgreSQL e registra o status inicial no Cassandra.
 *   Retorna o pedido criado com ID e total calculado.
 *
 * GET /api/pedidos/usuario/{usuarioId}
 *   Lista todos os pedidos de um usuário, do mais recente ao mais antigo.
 *   Usado na tela "Meus Pedidos" do frontend.
 *
 * GET /api/pedidos/{id}
 *   Busca os dados de um pedido específico (usuário, restaurante, total, status).
 *   Usado na tela de rastreamento.
 *
 * GET /api/pedidos/{id}/itens
 *   Lista os produtos e quantidades de um pedido.
 */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoServico;

    @PostMapping
    public ResponseEntity<?> criarPedido(@RequestBody PedidoRequest requisicao) {
        try {
            return ResponseEntity.ok(pedidoServico.criarPedido(requisicao));
        } catch (Exception excecao) {
            return ResponseEntity.badRequest().body(Map.of("erro", excecao.getMessage()));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Pedido>> listarPorUsuario(@PathVariable("usuarioId") Integer idUsuario) {
        return ResponseEntity.ok(pedidoServico.listarPorUsuario(idUsuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable("id") Integer idPedido) {
        try {
            return ResponseEntity.ok(pedidoServico.buscarPorId(idPedido));
        } catch (Exception excecao) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/itens")
    public ResponseEntity<List<ItemPedido>> listarItens(@PathVariable("id") Integer idPedido) {
        return ResponseEntity.ok(pedidoServico.listarItensPorPedido(idPedido));
    }
}
