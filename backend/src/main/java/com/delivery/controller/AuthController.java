package com.delivery.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delivery.dto.LoginRequest;
import com.delivery.model.postgres.Usuario;
import com.delivery.service.AuthService;
import com.delivery.dto.CadastroRequest;

import lombok.RequiredArgsConstructor;

/**
 * Controller de autenticação — expõe os endpoints de login e cadastro.
 *
 * @RestController → combina @Controller + @ResponseBody, ou seja,
 *   os métodos retornam JSON diretamente (não precisam de @ResponseBody).
 * @RequestMapping → prefixo base de todos os endpoints deste controller.
 *
 * POST /api/auth/login
 *   Recebe email e senha, valida no PostgreSQL.
 *   Sucesso → HTTP 200 com LoginResponse (id, nome, email, mensagem)
 *   Falha   → HTTP 401 com { "erro": "Email ou senha incorretos" }
 *
 * POST /api/auth/cadastro
 *   Recebe nome, email e senha, cria novo usuário no PostgreSQL.
 *   Sucesso → HTTP 200 com LoginResponse
 *   Falha   → HTTP 400 com { "erro": "Email já cadastrado" }
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService autenticacaoServico;

    @PostMapping("/login")
    public ResponseEntity<?> entrar(@RequestBody LoginRequest requisicao) {
        try {
            return ResponseEntity.ok(autenticacaoServico.entrar(requisicao));
        } catch (Exception excecao) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", excecao.getMessage()));
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity<?> cadastrar(@RequestBody CadastroRequest request) {
        try {
            Usuario usuario = new Usuario();
            usuario.setNome(request.getNome());
            usuario.setEmail(request.getEmail());
            usuario.setSenha(request.getSenha());
            return ResponseEntity.ok(autenticacaoServico.cadastrar(usuario));
        } catch (Exception excecao) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", excecao.getMessage()));
    }
}
}
