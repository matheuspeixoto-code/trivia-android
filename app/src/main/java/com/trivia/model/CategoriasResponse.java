package com.trivia.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoriasResponse {
    @SerializedName("trivia_categories")
    private List<Categoria> categorias;

    public List<Categoria> getCategorias() {
        return categorias;
    }
}