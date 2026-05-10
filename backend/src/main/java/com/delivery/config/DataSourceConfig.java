package com.delivery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Separa os repositórios por banco de dados.
 *
 * Por que isso é necessário?
 * Com três bancos ativos (PostgreSQL, MongoDB, Cassandra), o Spring precisa saber
 * qual repositório pertence a qual banco. Sem essa configuração, pode ocorrer
 * conflito e a aplicação não sobe.
 *
 * Cada anotação aponta para o pacote correto:
 *   postgres/   → Spring Data JPA  (PostgreSQL via Supabase)
 *   mongo/      → Spring Data MongoDB
 *   cassandra/  → Spring Data Cassandra
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.delivery.repository.postgres")
@EnableMongoRepositories(basePackages = "com.delivery.repository.mongo")
@EnableCassandraRepositories(basePackages = "com.delivery.repository.cassandra")
public class DataSourceConfig {
}
