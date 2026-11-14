
package com.trivia.model;

import java.io.Serializable;

public class Jogador implements Serializable {

    private long id;
    private String nome;

    public Jogador(long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
