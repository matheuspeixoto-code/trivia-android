
package com.trivia.model;

import java.io.Serializable;
import java.util.List;

public class Questao implements Serializable {

    private String categoria;
    private String dificuldade;
    private String enunciado;
    private String respostaCorreta;
    private List<String> respostasIncorretas;

    public Questao(String categoria, String dificuldade,
                   String enunciado, String respostaCorreta,
                   List<String> respostasIncorretas) {

        this.categoria = categoria;
        this.dificuldade = dificuldade;
        this.enunciado = enunciado;
        this.respostaCorreta = respostaCorreta;
        this.respostasIncorretas = respostasIncorretas;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(String dificuldade) {
        this.dificuldade = dificuldade;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getRespostaCorreta() {
        return respostaCorreta;
    }

    public void setRespostaCorreta(String respostaCorreta) {
        this.respostaCorreta = respostaCorreta;
    }

    public List<String> getRespostasIncorretas() {
        return respostasIncorretas;
    }

    public void setRespostasIncorretas(List<String> respostasIncorretas) {
        this.respostasIncorretas = respostasIncorretas;
    }
}
