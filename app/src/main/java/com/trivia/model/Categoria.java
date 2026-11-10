package com.trivia.model;

import com.google.gson.annotations.SerializedName;

public class Categoria {

    private int id;
    @SerializedName("name")
    private String nome;

    public Categoria(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}