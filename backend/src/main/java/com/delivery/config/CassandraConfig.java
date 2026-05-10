package com.delivery.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.oss.driver.api.core.CqlSession;

@Configuration
public class CassandraConfig {

    @Value("${spring.cassandra.keyspace-name}")
    private String keyspace;

    @Value("${astra.db.token}")
    private String token;

    @Bean
    public CqlSession cqlSession() {
        return CqlSession.builder()
                .withCloudSecureConnectBundle(
                    Paths.get("src/main/resources/secure-connect-database2.zip")
                )
                .withAuthCredentials("token", token)
                .withKeyspace(keyspace)
                .build();
    }

}