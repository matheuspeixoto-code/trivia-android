package com.trivia.controller;

public class MenuController {

    public String obterNomeJogador(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return "Jogador";
        }

        return nome.trim();
    }

    public String gerarMensagemBoasVindas(String nome) {
        return "Bem-vindo, " + obterNomeJogador(nome) + "!";
    }
}
