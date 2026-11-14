package com.trivia.api;

import com.trivia.model.Categoria;
import com.trivia.model.CategoriasResponse;
import com.trivia.model.QuestoesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api_category.php")
    Call<CategoriasResponse> buscarCategorias();

    @GET("api.php")
    Call<QuestoesResponse> buscarQuestoes(
            @Query("amount") int quantidade,
            @Query("category") Integer categoria,
            @Query("difficulty") String dificuldade,
            @Query("type") String tipo
    );

}
