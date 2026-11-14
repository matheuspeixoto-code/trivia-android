
package com.trivia.controller;

import com.trivia.model.Partida;
import com.trivia.model.Questao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JogoController {

    private final Partida partida;

    private int indiceAtual = 0;
    private int acertos = 0;
    private boolean respostaConfirmada = false;

    public JogoController(Partida partida) {
        this.partida = partida;
    }

    public Questao obterQuestaoAtual() {
        return partida.getQuestoes().get(indiceAtual);
    }

    public int getIndiceAtual() {
        return indiceAtual;
    }

    public int getQuantidadeQuestoes() {
        return partida.getQuestoes().size();
    }

    public int getAcertos() {
        return acertos;
    }

    public boolean isRespostaConfirmada() {
        return respostaConfirmada;
    }

    public List<String> obterAlternativasEmbaralhadas() {

        Questao questao = obterQuestaoAtual();

        List<String> alternativas = new ArrayList<>();

        alternativas.add(questao.getRespostaCorreta());

        if (questao.getRespostasIncorretas() != null) {
            alternativas.addAll(questao.getRespostasIncorretas());
        }

        Collections.shuffle(alternativas);

        return alternativas;
    }

    public ResultadoResposta verificarResposta(String respostaSelecionada) {

        if (respostaConfirmada || respostaSelecionada == null) {
            return null;
        }

        Questao questao = obterQuestaoAtual();

        String respostaCorreta = questao.getRespostaCorreta();

        boolean acertou = respostaSelecionada.equals(respostaCorreta);

        if (acertou) {
            acertos++;
        }

        respostaConfirmada = true;

        return new ResultadoResposta(acertou, respostaCorreta);
    }

    public String registrarTempoEsgotado() {

        if (respostaConfirmada) {
            return null;
        }

        respostaConfirmada = true;

        return obterQuestaoAtual().getRespostaCorreta();
    }

    public boolean avancarQuestao() {

        if (!respostaConfirmada) {
            return false;
        }

        indiceAtual++;

        if (indiceAtual < getQuantidadeQuestoes()) {
            respostaConfirmada = false;
            return true;
        }

        return false;
    }

    public void finalizarPartida() {

        partida.setQuantidadeAcertos(acertos);

        partida.setQuantidadeErros(
                getQuantidadeQuestoes() - acertos
        );

        partida.setPontuacao(acertos * 100);
    }

    public Partida getPartida() {
        return partida;
    }

    public static class ResultadoResposta {

        private final boolean acertou;
        private final String respostaCorreta;

        public ResultadoResposta(boolean acertou, String respostaCorreta) {
            this.acertou = acertou;
            this.respostaCorreta = respostaCorreta;
        }

        public boolean isAcertou() {
            return acertou;
        }

        public String getRespostaCorreta() {
            return respostaCorreta;
        }
    }
}
