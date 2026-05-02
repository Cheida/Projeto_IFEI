package com.delivery.service;

import com.delivery.dto.LoginRequest;
import com.delivery.dto.LoginResponse;
import com.delivery.model.postgres.Usuario;
import com.delivery.repository.postgres.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Serviço responsável por autenticação de usuários (PostgreSQL).
 *
 * @RequiredArgsConstructor → Lombok gera o construtor com os campos final,
 * que o Spring usa para injetar o UsuarioRepository automaticamente.
 *
 * login(): busca o usuário por email + senha no PostgreSQL.
 *   Se não encontrar, lança RuntimeException com mensagem de erro.
 *   O controller captura essa exceção e retorna HTTP 401.
 *
 * cadastro(): verifica se o email já existe antes de salvar.
 *   Se existir, lança RuntimeException → controller retorna HTTP 400.
 *   dataHora é preenchida automaticamente aqui (não vem do frontend).
 *
 * Nota de segurança: em produção, a senha deveria ser armazenada
 * com hash BCrypt, nunca em texto puro como está aqui.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepositorio;

    public LoginResponse entrar(LoginRequest requisicao) {
        return usuarioRepositorio.buscarPorEmailESenha(requisicao.getEmail(), requisicao.getSenha())
                .map(usuario -> new LoginResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), "Login realizado com sucesso"))
                .orElseThrow(() -> new RuntimeException("Email ou senha incorretos"));
    }

    public LoginResponse cadastrar(Usuario usuario) {
        if (usuarioRepositorio.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }
        usuario.setDataHora(LocalDateTime.now());
        Usuario usuarioSalvo = usuarioRepositorio.save(usuario);
        return new LoginResponse(usuarioSalvo.getId(), usuarioSalvo.getNome(), usuarioSalvo.getEmail(), "Cadastro realizado com sucesso");
    }
}
