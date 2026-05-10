package com.delivery.service;

import com.delivery.model.mongo.Cardapio;
import com.delivery.repository.mongo.CardapioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pelos cardápios armazenados no MongoDB.
 *
 * Por que MongoDB para cardápios?
 * O cardápio tem estrutura hierárquica (restaurante → categorias → produtos)
 * com campos flexíveis como "disponivel". Em SQL, isso precisaria de 3 tabelas.
 * No MongoDB, tudo fica em um único documento JSON, facilitando leitura e escrita.
 *
 * listarTodos(): retorna todos os cardápios (todos os 10 restaurantes).
 *
 * buscarPorRestaurante(): retorna Optional<Cardapio> — pode estar vazio
 * se o restaurante não tiver cardápio no MongoDB.
 * O frontend trata esse caso exibindo os produtos do PostgreSQL como fallback.
 */
@Service
@RequiredArgsConstructor
public class CardapioService {

    private final CardapioRepository cardapioRepositorio;

    public List<Cardapio> listarTodos() {
        return cardapioRepositorio.findAll();
    }

    public Optional<Cardapio> buscarPorRestaurante(Integer idRestaurante) {
        return cardapioRepositorio.buscarPorIdRestaurante(idRestaurante);
    }
}
