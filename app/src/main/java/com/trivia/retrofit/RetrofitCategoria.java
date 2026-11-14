package com.trivia.retrofit;

import com.trivia.api.ApiService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCategoria {

    private static final String BASE_URL = "https://opentdb.com/";
    private static Retrofit retrofit;
    private static final OkHttpClient client = new OkHttpClient.Builder().build();

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
    public static ApiService getApiService() {
        return getInstance().create(ApiService.class);
    }
}
