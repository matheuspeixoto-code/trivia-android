
package com.trivia.model;

import java.io.Serializable;
import java.util.List;

public class Partida implements Serializable {

    private long id;
    private Jogador jogador;
    private List<Questao> questoes;
    private int pontuacao;
    private int quantidadeAcertos;
    private int quantidadeErros;
    private int tempoPorQuestao;
    private int indiceQuestaoAtual;

    public Partida(long id, Jogador jogador, List<Questao> questoes,
                   int pontuacao, int quantidadeAcertos,
                   int quantidadeErros, int tempoPorQuestao) {

        this.id = id;
        this.jogador = jogador;
        this.questoes = questoes;
        this.pontuacao = pontuacao;
        this.quantidadeAcertos = quantidadeAcertos;
        this.quantidadeErros = quantidadeErros;
        this.tempoPorQuestao = tempoPorQuestao;
        this.indiceQuestaoAtual = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Jogador getJogador() {
        return jogador;
    }

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
    }

    public List<Questao> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(List<Questao> questoes) {
        this.questoes = questoes;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public int getQuantidadeAcertos() {
        return quantidadeAcertos;
    }

    public void setQuantidadeAcertos(int quantidadeAcertos) {
        this.quantidadeAcertos = quantidadeAcertos;
    }

    public int getQuantidadeErros() {
        return quantidadeErros;
    }

    public void setQuantidadeErros(int quantidadeErros) {
        this.quantidadeErros = quantidadeErros;
    }

    public int getTempoPorQuestao() {
        return tempoPorQuestao;
    }

    public int getIndiceQuestaoAtual() {
        return indiceQuestaoAtual;
    }

    public void avancarQuestao() {
        indiceQuestaoAtual++;
    }
}
