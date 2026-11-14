package com.trivia.controller;

public class MainController {

    public String validarNome(String nome) {
        if (nome == null) {
            return null;
        }

        String nomeFormatado = nome.trim();

        if (nomeFormatado.isEmpty()) {
            return null;
        }

        return nomeFormatado;
    }
}
