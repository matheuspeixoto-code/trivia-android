package com.trivia.controller;

public class ResultadoController {

    private final String nomeJogador;
    private final int totalQuestoes;
    private final int acertos;
    private final int pontuacao;

    public ResultadoController(
            String nomeJogador,
            int totalQuestoes,
            int acertos,
            int pontuacao
    ) {
        this.nomeJogador = nomeJogador;
        this.totalQuestoes = totalQuestoes;
        this.acertos = acertos;
        this.pontuacao = pontuacao;
    }

    public String getNomeJogador() {
        return nomeJogador;
    }

    public int getTotalQuestoes() {
        return totalQuestoes;
    }

    public int getAcertos() {
        return acertos;
    }

    public int getErros() {
        return totalQuestoes - acertos;
    }

    public int getPontuacao() {
        return pontuacao;
    }
}
