package com.delivery.service;

import com.delivery.model.cassandra.*;
import com.delivery.model.postgres.Pedido;
import com.delivery.repository.cassandra.EventoPedidoRepository;
import com.delivery.repository.cassandra.LogSistemaRepository;
import com.delivery.repository.cassandra.StatusPedidoRepository;
import com.delivery.repository.postgres.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável pelo rastreamento de pedidos — integra PostgreSQL e Cassandra.
 *
 * atualizarStatus(): atualiza o status em DOIS lugares:
 *   1. PostgreSQL (tabela pedidos): campo status é atualizado para o valor atual
 *   2. Cassandra (status_pedido): uma nova linha é INSERIDA com o novo status + timestamp
 *      → o Cassandra nunca edita, só acrescenta, preservando o histórico completo
 *   Também grava um evento e um log no Cassandra.
 *
 * buscarStatus(): retorna o histórico de status (todas as mudanças) do Cassandra.
 *   Como o CLUSTERING ORDER BY é DESCENDING, o mais recente vem primeiro.
 *
 * buscarEventos(): retorna os eventos do pedido (PEDIDO_CRIADO, STATUS_ATUALIZADO...).
 *
 * buscarLogs(): retorna os logs de um serviço específico do Cassandra.
 *   Acessível via GET /api/status/logs?servico=PedidoService
 */
@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusPedidoRepository statusPedidoRepositorio;
    private final EventoPedidoRepository eventoPedidoRepositorio;
    private final LogSistemaRepository logSistemaRepositorio;
    private final PedidoRepository pedidoRepositorio;

    public List<StatusPedido> buscarStatus(Integer idPedido) {
        UUID idPedidoCassandra = UUID.nameUUIDFromBytes(idPedido.toString().getBytes());
        return statusPedidoRepositorio.buscarPorIdPedido(idPedidoCassandra);
    }

    public StatusPedido atualizarStatus(Integer idPedido, String novoEstado, String localizacao) {
        // 1. atualiza no PostgreSQL (estado atual do pedido)
        Pedido pedido = pedidoRepositorio.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
        pedido.setStatus(novoEstado);
        pedidoRepositorio.save(pedido);

        // 2. insere nova entrada no Cassandra (histórico de mudanças)
        UUID idPedidoCassandra = UUID.nameUUIDFromBytes(idPedido.toString().getBytes());
        Instant agora = Instant.now();

        StatusPedidoKey chaveStatus = new StatusPedidoKey();
        chaveStatus.setIdPedido(idPedidoCassandra);
        chaveStatus.setTimestamp(agora);
        StatusPedido statusPedido = new StatusPedido();
        statusPedido.setChave(chaveStatus);
        statusPedido.setStatus(novoEstado);
        statusPedido.setLocalizacao(localizacao != null && !localizacao.isEmpty() ? localizacao : "Em trânsito");
        statusPedidoRepositorio.save(statusPedido);

        // evento de atualização
        EventoPedidoKey chaveEvento = new EventoPedidoKey();
        chaveEvento.setIdPedido(idPedidoCassandra);
        chaveEvento.setHorarioEvento(agora.plusMillis(1)); // +1ms evita colisão de chave
        EventoPedido evento = new EventoPedido();
        evento.setChave(chaveEvento);
        evento.setTipoEvento("STATUS_ATUALIZADO");
        evento.setDescricao("Status atualizado para: " + novoEstado);
        eventoPedidoRepositorio.save(evento);

        // log do serviço
        LogSistemaKey chaveLog = new LogSistemaKey();
        chaveLog.setNomeServico("StatusService");
        chaveLog.setHorarioLog(agora.plusMillis(2));
        LogSistema log = new LogSistema();
        log.setChave(chaveLog);
        log.setIdPedido(idPedidoCassandra);
        log.setMensagem("Pedido #" + idPedido + " -> status: " + novoEstado);
        log.setNivel("INFO");
        logSistemaRepositorio.save(log);

        return statusPedido;
    }

    public List<EventoPedido> buscarEventos(Integer idPedido) {
        UUID idPedidoCassandra = UUID.nameUUIDFromBytes(idPedido.toString().getBytes());
        return eventoPedidoRepositorio.buscarPorIdPedido(idPedidoCassandra);
    }

    public List<LogSistema> buscarLogs(String servico) {
        return logSistemaRepositorio.buscarPorNomeServico(servico);
    }
}
