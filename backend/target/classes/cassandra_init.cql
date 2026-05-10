-- Execute este arquivo antes de iniciar o backend:
-- cqlsh -f src/main/resources/cassandra_init.cql



USE idei;

CREATE TABLE IF NOT EXISTS status_pedido (
    id_pedido UUID,
    timestamp TIMESTAMP,
    status TEXT,
    localizacao TEXT,
    PRIMARY KEY (id_pedido, timestamp)
) WITH CLUSTERING ORDER BY (timestamp DESC);

CREATE TABLE IF NOT EXISTS eventos_pedido (
    id_pedido UUID,
    horario_evento TIMESTAMP,
    tipo_evento TEXT,
    descricao TEXT,
    PRIMARY KEY (id_pedido, horario_evento)
) WITH CLUSTERING ORDER BY (horario_evento DESC);

CREATE TABLE IF NOT EXISTS logs_sistema (
    nome_servico TEXT,
    horario_log TIMESTAMP,
    id_pedido UUID,
    mensagem TEXT,
    nivel TEXT,
    PRIMARY KEY (nome_servico, horario_log)
) WITH CLUSTERING ORDER BY (horario_log DESC);
