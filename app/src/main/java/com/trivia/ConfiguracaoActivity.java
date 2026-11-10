package com.trivia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.trivia.api.ApiService;
import com.trivia.model.Categoria;
import com.trivia.model.CategoriasResponse;
import com.trivia.retrofit.RetrofitCategoria;
import com.trivia.utils.ConfiguracaoUtils;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfiguracaoActivity extends AppCompatActivity

{

    private LinearLayout layoutCarregando;
    private LinearLayout layoutConfiguracao;
    private Spinner spDificuldade;
    private Spinner spTipo;

    private Spinner spCategoria;
    private EditText edtQuantidade;
    private SeekBar seekTempo;
    private Button btnIniciarPartida;
    private static final String TAG = "ConfiguracaoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        layoutCarregando = findViewById(R.id.layoutCarregando);
        layoutConfiguracao = findViewById(R.id.layoutConfiguracao);

        edtQuantidade = findViewById(R.id.edtQuantidade);
        seekTempo = findViewById(R.id.seekTempo);
        btnIniciarPartida = findViewById(R.id.btnIniciarPartida);
        spCategoria = findViewById(R.id.spCategoria);

        configurarDificuldade();
        configurarTipo();
        carregarCategorias();

        btnIniciarPartida.setOnClickListener(v -> {
            obterConfiguracao();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() chamado");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() chamado");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() chamado");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() chamado");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() chamado");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() chamado");
    }


    private void configurarDificuldade() {

        spDificuldade = findViewById(R.id.spDificuldade);

        String[] dificuldades = {
                "Any Difficult",
                "Easy",
                "Medium",
                "Hard"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                dificuldades
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spDificuldade.setAdapter(adapter);
    }


    private void configurarTipo() {

        spTipo = findViewById(R.id.spTipo);

        String[] tipo = {
                "Any Type",
                "Multiple Choice",
                "True / False"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                tipo
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spTipo.setAdapter(adapter);
    }

    private void obterConfiguracao() {

        String dificuldade = ConfiguracaoUtils.obterDificuldade(
                spDificuldade.getSelectedItemPosition()
        );

        String tipo = ConfiguracaoUtils.obterTipo(
                spTipo.getSelectedItemPosition()
        );

        Categoria categoriaSelecionada =
                (Categoria) spCategoria.getSelectedItem();

        Integer categoriaId = categoriaSelecionada.getId() == 0
                ? null
                : categoriaSelecionada.getId();

        String textoQuantidade = edtQuantidade.getText().toString().trim();

        if (textoQuantidade.isEmpty()) {
            edtQuantidade.setError("Informe a quantidade de questões");
            return;
        }

        int quantidade = Integer.parseInt(textoQuantidade);

        if (quantidade < 1 || quantidade > 50) {
            edtQuantidade.setError("Informe um valor entre 1 e 50");
            return;
        }

        int tempo = seekTempo.getProgress() + 10;

        Log.d(TAG, "Dificuldade: " + dificuldade);
        Log.d(TAG, "Tipo: " + tipo);
        Log.d(TAG, "Quantidade: " + quantidade);
        Log.d(TAG, "Tempo por questão: " + tempo);
        Log.d(TAG, "Categoria: " + categoriaId);
    }


    private void carregarCategorias() {

        Log.d(TAG, "Método carregarCategorias() foi chamado!");

        ApiService api = RetrofitCategoria.getApiService();

        Log.d(TAG, "ApiService criado!");

        Call<CategoriasResponse> chamada = api.buscarCategorias();

        Log.d(TAG, "Preparando requisição...");


        chamada.enqueue(new Callback<CategoriasResponse>() {

            @Override
            public void onResponse(
                    Call<CategoriasResponse> call,
                    Response<CategoriasResponse> response) {
                Log.d(TAG, "Código HTTP: " + response.code());
                Log.d(TAG, "Body: " + response.body());

                if (response.body() != null) {
                    Log.d(TAG, "Categorias: " + response.body().getCategorias());
                }

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getCategorias() != null) {

                    List<Categoria> categorias =
                            new ArrayList<>();

                    categorias.add(new Categoria(0, "Todas as categorias"));

                    categorias.addAll(response.body().getCategorias());

                    ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(
                            ConfiguracaoActivity.this,
                            android.R.layout.simple_spinner_item,
                            categorias
                    );

                    adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                    );

                    spCategoria.setAdapter(adapter);


                    layoutCarregando.setVisibility(View.GONE);


                    layoutConfiguracao.setVisibility(View.VISIBLE);

                } else {
                    Log.e(TAG, "Erro ao carregar categorias. Código: "
                            + response.code());
                }
            }

            @Override
            public void onFailure(
                    Call<CategoriasResponse> call,
                    Throwable t) {

                Log.e(TAG, "Falha na conexão ao buscar categorias", t);
            }
        });
    }

}
