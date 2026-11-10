package com.trivia.api;

import com.trivia.model.Categoria;
import com.trivia.model.CategoriasResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("api_category.php")
    Call<CategoriasResponse> buscarCategorias();

}
