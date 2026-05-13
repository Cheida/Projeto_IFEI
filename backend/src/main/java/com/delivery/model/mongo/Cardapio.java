package com.delivery.model.mongo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * Documento MongoDB que representa o cardápio de um restaurante.
 *
 * Por que MongoDB aqui?
 * O cardápio tem estrutura aninhada e flexível (categorias → produtos),
 * o que se adapta naturalmente ao modelo de documento JSON do MongoDB.
 * Em SQL relacional, isso exigiria três tabelas e vários JOINs.
 *
 * Estrutura do documento salvo no MongoDB:
 * {
 *   restaurante_id: 1,
 *   nome_restaurante: "Burger House",
 *   categorias: [
 *     { nome: "Lanches", produtos: [ { nome: "Hamburguer", preco_base: 25, disponivel: true } ] }
 *   ]
 * }
 *
 * @JsonAlias  → aceita "restaurante_id" (snake_case do JSON) na leitura,
 *               mas serializa como "restauranteId" (camelCase) na resposta da API
 * @Field      → define o nome do campo dentro do documento MongoDB
 *
 * Coleção: cardapio  |  Banco: ifei_delivery
 * Para importar os dados: mongoimport --db ifei_delivery --collection cardapio --file cardapio.seed.json --jsonArray
 */
@Document(collection = "cardapio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cardapio {

    @Id
    private String id;

    @JsonAlias("restaurante_id")
    @JsonProperty("restauranteId")
    @Field("restaurante_id")
    private Integer idRestaurante;

    @JsonAlias("nome_restaurante")
    @JsonProperty("nomeRestaurante")
    @Field("nome_restaurante")
    private String nomeRestaurante;

    private List<Categoria> categorias;

    /** Categoria do cardápio (ex: Lanches, Pizzas, Sobremesas) */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Categoria {
        private String nome;
        private List<Produto> produtos;

        /** Produto dentro de uma categoria do cardápio */
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Produto {
            private String nome;

            @JsonAlias("preco_base")
            @JsonProperty("precoBase")
            @Field("preco_base")
            private Double precoBase;

            private Boolean disponivel; // se false, aparece como "Indisponível" no frontend
        }
    }
}
