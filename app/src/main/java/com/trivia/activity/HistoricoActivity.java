package com.trivia.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.trivia.R;

import androidx.appcompat.app.AppCompatActivity;

import com.trivia.database.PartidaRepositorio;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoricoActivity extends AppCompatActivity {

    private LinearLayout layoutCarregando;
    private LinearLayout layoutHistorico;

    private ListView listHistorico;

    private String nomeJogador;
    private TextView txtHistoricoVazio;
    private Button btnVoltarMenu;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);
        nomeJogador = getIntent().getStringExtra("nome_jogador");


        inicializarComponentes();
        configurarBotoes();

        carregarHistorico();
    }

    private void inicializarComponentes() {

        layoutCarregando = findViewById(R.id.layoutCarregando);
        layoutHistorico = findViewById(R.id.layoutHistorico);

        listHistorico = findViewById(R.id.listHistorico);
        txtHistoricoVazio = findViewById(R.id.txtHistoricoVazio);

        btnVoltarMenu = findViewById(R.id.btnVoltarMenu);
    }

    private void configurarBotoes() {

        btnVoltarMenu.setOnClickListener(v -> finish());
    }

    private void carregarHistorico() {

        layoutCarregando.setVisibility(View.VISIBLE);
        layoutHistorico.setVisibility(View.GONE);

        executor.execute(() -> {

            ArrayList<String> partidas = new ArrayList<>();

            boolean consultaRealizada = false;

            try {

                PartidaRepositorio repositorio =
                        new PartidaRepositorio(this);

                try (Cursor cursor = repositorio.listarHistorico(nomeJogador)) {

                    if (cursor != null) {

                        while (cursor.moveToNext()) {

                            String nome = cursor.getString(
                                    cursor.getColumnIndexOrThrow("nome_jogador")
                            );

                            String data = cursor.getString(
                                    cursor.getColumnIndexOrThrow("data")
                            );

                            int quantidade = cursor.getInt(
                                    cursor.getColumnIndexOrThrow("quantidade_questoes")
                            );

                            int acertos = cursor.getInt(
                                    cursor.getColumnIndexOrThrow("acertos")
                            );

                            int erros = quantidade - acertos;

                            int pontuacao = cursor.getInt(
                                    cursor.getColumnIndexOrThrow("pontuacao")
                            );

                            String item =
                                    "Jogador: " + nome +
                                            "\nData: " + data +
                                            "\nQuestões: " + quantidade +
                                            "\nAcertos: " + acertos +
                                            " | Erros: " + erros +
                                            "\nPontuação: " + pontuacao;

                            partidas.add(item);
                        }

                        consultaRealizada = true;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean sucesso = consultaRealizada;

            runOnUiThread(() -> {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                layoutCarregando.setVisibility(View.GONE);
                layoutHistorico.setVisibility(View.VISIBLE);

                if (!sucesso) {

                    txtHistoricoVazio.setText(
                            "Não foi possível carregar o histórico."
                    );

                    txtHistoricoVazio.setVisibility(View.VISIBLE);
                    listHistorico.setVisibility(View.GONE);

                } else if (partidas.isEmpty()) {

                    txtHistoricoVazio.setText(
                            "Nenhuma partida encontrada."
                    );

                    txtHistoricoVazio.setVisibility(View.VISIBLE);
                    listHistorico.setVisibility(View.GONE);

                } else {

                    txtHistoricoVazio.setVisibility(View.GONE);
                    listHistorico.setVisibility(View.VISIBLE);

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_list_item_1,
                                    partidas
                            );

                    listHistorico.setAdapter(adapter);
                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onDestroy() {
        executor.shutdown();
        super.onDestroy();
    }
}
