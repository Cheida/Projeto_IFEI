package com.delivery.service;

import com.delivery.dto.ItemPedidoRequest;
import com.delivery.dto.PedidoRequest;
import com.delivery.model.cassandra.*;
import com.delivery.model.postgres.*;
import com.delivery.repository.cassandra.EventoPedidoRepository;
import com.delivery.repository.cassandra.LogSistemaRepository;
import com.delivery.repository.cassandra.StatusPedidoRepository;
import com.delivery.repository.postgres.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Serviço principal de pedidos — integra PostgreSQL e Cassandra.
 *
 * criarPedido() demonstra o uso de múltiplos bancos em uma única operação:
 *
 *   1. PostgreSQL: valida usuário e restaurante, salva o pedido e os itens,
 *      calcula o total somando (preço × quantidade) de cada produto.
 *      @Transactional garante que se qualquer operação JPA falhar, tudo é revertido.
 *
 *   2. Cassandra: registra o status inicial ("pendente"), o evento "PEDIDO_CRIADO"
 *      e um log de sistema. O bloco Cassandra está em try-catch separado —
 *      se o Cassandra não estiver disponível, o pedido ainda é criado no PostgreSQL.
 *
 * UUID: o Cassandra usa UUID como identificador, mas o PostgreSQL usa inteiro.
 * Usamos UUID.nameUUIDFromBytes(id.toString().getBytes()) para gerar um UUID
 * determinístico a partir do ID inteiro — o mesmo ID sempre gera o mesmo UUID.
 *
 * listarPorUsuario(): busca os pedidos do usuário ordenados por data (mais recente primeiro).
 * listarItensPorPedido(): retorna os itens de um pedido específico.
 */
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepositorio;
    private final ItemPedidoRepository itemPedidoRepositorio;
    private final ProdutoRepository produtoRepositorio;
    private final UsuarioRepository usuarioRepositorio;
    private final RestauranteRepository restauranteRepositorio;
    private final StatusPedidoRepository statusPedidoRepositorio;
    private final EventoPedidoRepository eventoPedidoRepositorio;
    private final LogSistemaRepository logSistemaRepositorio;

    @Transactional // garante atomicidade nas operações PostgreSQL
    public Pedido criarPedido(PedidoRequest requisicao) {
        Usuario usuario = usuarioRepositorio.findById(requisicao.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Restaurante restaurante = restauranteRepositorio.findById(requisicao.getIdRestaurante())
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));

        // salva o pedido primeiro para obter o ID gerado pelo PostgreSQL
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setRestaurante(restaurante);
        pedido.setStatus("pendente");
        pedido.setDataHora(LocalDateTime.now());
        pedido.setTotal(BigDecimal.ZERO);
        pedido = pedidoRepositorio.save(pedido);

        // processa os itens e calcula o total
        BigDecimal total = BigDecimal.ZERO;
        List<ItemPedido> itens = new ArrayList<>();

        for (ItemPedidoRequest itemRequisicao : requisicao.getItens()) {
            Produto produto = produtoRepositorio.findById(itemRequisicao.getIdProduto())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + itemRequisicao.getIdProduto()));

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemRequisicao.getQuantidade());
            itens.add(itemPedido);

            total = total.add(produto.getPreco().multiply(BigDecimal.valueOf(itemRequisicao.getQuantidade())));
        }

        itemPedidoRepositorio.saveAll(itens);
        pedido.setTotal(total);
        pedido = pedidoRepositorio.save(pedido); // atualiza com o total calculado

        // registra no Cassandra (fora da transação JPA, com tolerância a falhas)
        salvarNoCassandra(pedido, itens.size());

        return pedido;
    }

    /** Grava status inicial, evento e log no Cassandra. Falha silenciosa se Cassandra estiver offline. */
    private void salvarNoCassandra(Pedido pedido, int quantidadeItens) {
        try {
            UUID idPedidoCassandra = UUID.nameUUIDFromBytes(pedido.getId().toString().getBytes());
            Instant agora = Instant.now();

            // status inicial do pedido
            StatusPedidoKey chaveStatus = new StatusPedidoKey();
            chaveStatus.setIdPedido(idPedidoCassandra);
            chaveStatus.setTimestamp(agora);
            StatusPedido statusPedido = new StatusPedido();
            statusPedido.setChave(chaveStatus);
            statusPedido.setStatus("pendente");
            statusPedido.setLocalizacao("Restaurante");
            statusPedidoRepositorio.save(statusPedido);

            // evento de criação do pedido
            EventoPedidoKey chaveEvento = new EventoPedidoKey();
            chaveEvento.setIdPedido(idPedidoCassandra);
            chaveEvento.setHorarioEvento(agora);
            EventoPedido evento = new EventoPedido();
            evento.setChave(chaveEvento);
            evento.setTipoEvento("PEDIDO_CRIADO");
            evento.setDescricao("Pedido criado com " + quantidadeItens + " item(ns). Total: R$ " + pedido.getTotal());
            eventoPedidoRepositorio.save(evento);

            // log do sistema
            LogSistemaKey chaveLog = new LogSistemaKey();
            chaveLog.setNomeServico("PedidoService");
            chaveLog.setHorarioLog(agora.plusMillis(1)); // +1ms evita colisão de chave
            LogSistema log = new LogSistema();
            log.setChave(chaveLog);
            log.setIdPedido(idPedidoCassandra);
            log.setMensagem("Pedido #" + pedido.getId() + " criado. Total: R$ " + pedido.getTotal());
            log.setNivel("INFO");
            logSistemaRepositorio.save(log);

        } catch (Exception excecao) {
            // Cassandra offline não impede a criação do pedido no PostgreSQL
            System.err.println("[Cassandra] Não foi possível registrar o pedido: " + excecao.getMessage());
        }
    }

    public List<Pedido> listarPorUsuario(Integer idUsuario) {
        return pedidoRepositorio.buscarPorIdUsuarioOrdenadoPorDataHoraDesc(idUsuario);
    }

    public Pedido buscarPorId(Integer idPedido) {
        return pedidoRepositorio.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }

    public List<ItemPedido> listarItensPorPedido(Integer idPedido) {
        return itemPedidoRepositorio.buscarPorIdPedido(idPedido);
    }
}
