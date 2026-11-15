package com.trivia.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.trivia.R;
import com.trivia.controller.HistoricoController;

import java.util.List;

public class HistoricoActivity extends AppCompatActivity {

    private static final String TAG = "HistoricoActivity";

    private LinearLayout layoutCarregando;
    private LinearLayout layoutHistorico;

    private ListView listHistorico;
    private TextView txtHistoricoVazio;
    private Button btnVoltarMenu;

    private String nomeJogador;

    private HistoricoController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        nomeJogador = getIntent().getStringExtra("nome_jogador");

        inicializarComponentes();

        controller = new HistoricoController(this);

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

        controller.carregarHistorico(
                nomeJogador,
                new HistoricoController.HistoricoCallback() {

                    @Override
                    public void onSucesso(List<String> partidas) {

                        exibirHistorico(partidas);
                    }

                    @Override
                    public void onErro() {

                        exibirMensagemHistorico(
                                "Não foi possível carregar o histórico."
                        );
                    }
                }
        );
    }

    private void exibirHistorico(List<String> partidas) {

        layoutCarregando.setVisibility(View.GONE);
        layoutHistorico.setVisibility(View.VISIBLE);

        if (partidas.isEmpty()) {

            exibirMensagemHistorico(
                    "Nenhuma partida encontrada."
            );

            return;
        }

        txtHistoricoVazio.setVisibility(View.GONE);
        listHistorico.setVisibility(View.VISIBLE);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.item_historico,
                partidas
        );

        listHistorico.setAdapter(adapter);
    }

    private void exibirMensagemHistorico(String mensagem) {

        layoutCarregando.setVisibility(View.GONE);
        layoutHistorico.setVisibility(View.VISIBLE);

        txtHistoricoVazio.setText(mensagem);
        txtHistoricoVazio.setVisibility(View.VISIBLE);

        listHistorico.setVisibility(View.GONE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {

        if (controller != null) {
            controller.encerrar();
        }

        super.onDestroy();

        Log.d(TAG, "onDestroy");
    }
}
