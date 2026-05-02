package com.delivery.controller;

import com.delivery.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller de rastreamento — consome dados do Cassandra.
 *
 * GET /api/status/pedido/{pedidoId}
 *   Retorna o histórico completo de status do pedido (do Cassandra).
 *   Cada entrada tem: status, localização e timestamp.
 *   Usado na tela de tracking para mostrar a linha do tempo.
 *
 * PUT /api/status/pedido/{pedidoId}?status=preparando&localizacao=Cozinha
 *   Atualiza o status no PostgreSQL (campo atual) e insere nova entrada no Cassandra.
 *   Chamado pelos botões "Pendente / Preparando / Entregue" na tela de tracking.
 *
 * GET /api/status/pedido/{pedidoId}/eventos
 *   Retorna os eventos do pedido (PEDIDO_CRIADO, STATUS_ATUALIZADO...) do Cassandra.
 *
 * GET /api/status/logs?servico=PedidoService
 *   Retorna os logs de sistema de um serviço específico do Cassandra.
 *   Útil para depuração e apresentação do projeto.
 */
@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusServico;

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<?> buscarStatus(@PathVariable("pedidoId") Integer idPedido) {
        try {
            return ResponseEntity.ok(statusServico.buscarStatus(idPedido));
        } catch (Exception excecao) {
            return ResponseEntity.badRequest().body(Map.of("erro", excecao.getMessage()));
        }
    }

    @PutMapping("/pedido/{pedidoId}")
    public ResponseEntity<?> atualizarStatus(
            @PathVariable("pedidoId") Integer idPedido,
            @RequestParam("status") String novoEstado,
            @RequestParam(required = false, defaultValue = "") String localizacao) {
        try {
            return ResponseEntity.ok(statusServico.atualizarStatus(idPedido, novoEstado, localizacao));
        } catch (Exception excecao) {
            return ResponseEntity.badRequest().body(Map.of("erro", excecao.getMessage()));
        }
    }

    @GetMapping("/pedido/{pedidoId}/eventos")
    public ResponseEntity<?> buscarEventos(@PathVariable("pedidoId") Integer idPedido) {
        try {
            return ResponseEntity.ok(statusServico.buscarEventos(idPedido));
        } catch (Exception excecao) {
            return ResponseEntity.badRequest().body(Map.of("erro", excecao.getMessage()));
        }
    }

    @GetMapping("/logs")
    public ResponseEntity<?> buscarLogs(
            @RequestParam(defaultValue = "PedidoService") String servico) {
        try {
            return ResponseEntity.ok(statusServico.buscarLogs(servico));
        } catch (Exception excecao) {
            return ResponseEntity.badRequest().body(Map.of("erro", excecao.getMessage()));
        }
    }
}
