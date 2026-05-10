package com.delivery.service;

import com.delivery.model.postgres.Produto;
import com.delivery.model.postgres.Restaurante;
import com.delivery.repository.postgres.ProdutoRepository;
import com.delivery.repository.postgres.RestauranteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço responsável por restaurantes e seus produtos (PostgreSQL).
 *
 * listarTodos(): retorna todos os 10 restaurantes cadastrados.
 *   Usado na tela Home do frontend para exibir os cards de restaurantes.
 *
 * buscarPorId(): busca um restaurante pelo ID.
 *   Lança RuntimeException se não encontrar → controller retorna HTTP 404.
 *
 * listarProdutos(): retorna os produtos de um restaurante específico.
 *   É usado como FALLBACK quando o MongoDB não tem o cardápio daquele restaurante.
 *   Se o MongoDB tiver, o frontend usa o cardápio do MongoDB (mais rico, com categorias).
 */
@Service
@RequiredArgsConstructor
public class RestauranteService {

    private final RestauranteRepository restauranteRepositorio;
    private final ProdutoRepository produtoRepositorio;

    public List<Restaurante> listarTodos() {
        return restauranteRepositorio.findAll();
    }

    public Restaurante buscarPorId(Integer id) {
        return restauranteRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));
    }

    public List<Produto> listarProdutos(Integer idRestaurante) {
        return produtoRepositorio.buscarPorIdRestaurante(idRestaurante);
    }
}
