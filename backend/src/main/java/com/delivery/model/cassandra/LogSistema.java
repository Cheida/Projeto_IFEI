package com.delivery.model.cassandra;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

/**
 * Tabela Cassandra que armazena logs do sistema.
 *
 * Funciona como um "diário" da aplicação: registra o que cada serviço fez,
 * associando a ação ao pedido relacionado.
 *
 * Exemplos de logs gerados automaticamente:
 *   INFO  | PedidoService  | "Pedido #5 criado com sucesso. Total: R$ 40.00"
 *   INFO  | StatusService  | "Pedido #5 -> status: preparando"
 *
 * O campo "nivel" pode ser: INFO, WARN, ERROR.
 *
 * Tabela: logs_sistema(nome_servico, horario_log, id_pedido, mensagem, nivel)
 */
@Table("logs_sistema")
@Data
public class LogSistema {

    @PrimaryKey
    @JsonProperty("key")
    private LogSistemaKey chave; // nome_servico + horario_log

    @Column("id_pedido")
    private UUID idPedido; // referência ao pedido relacionado (UUID derivado do ID inteiro)

    @Column("mensagem")
    private String mensagem;

    @Column("nivel")
    private String nivel; // INFO, WARN ou ERROR
}
