package com.trivia.model;

public class ConfiguracaoPartida {

    private int categoria;
    private String dificuldade;
    private String tipo;
    private int quantidadeQuestoes;
    private int tempoPorQuestao;

    public ConfiguracaoPartida(
            int categoria,
            String dificuldade,
            String tipo,
            int quantidadeQuestoes,
            int tempoPorQuestao) {

        this.categoria = categoria;
        this.dificuldade = dificuldade;
        this.tipo = tipo;
        this.quantidadeQuestoes = quantidadeQuestoes;
        this.tempoPorQuestao = tempoPorQuestao;
    }


    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(String dificuldade) {
        this.dificuldade = dificuldade;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getQuantidadeQuestoes() {
        return quantidadeQuestoes;
    }

    public void setQuantidadeQuestoes(int quantidadeQuestoes) {
        this.quantidadeQuestoes = quantidadeQuestoes;
    }

    public int getTempoPorQuestao() {
        return tempoPorQuestao;
    }

    public void setTempoPorQuestao(int tempoPorQuestao) {
        this.tempoPorQuestao = tempoPorQuestao;
    }
}