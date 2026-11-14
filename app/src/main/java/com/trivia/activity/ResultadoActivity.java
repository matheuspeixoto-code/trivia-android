
package com.trivia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.trivia.R;
import com.trivia.controller.ResultadoController;

public class ResultadoActivity extends AppCompatActivity {

    private TextView txtNomeJogador;
    private TextView txtTotalQuestoes;
    private TextView txtAcertos;
    private TextView txtErros;
    private TextView txtPontuacao;

    private Button btnJogarNovamente;
    private Button btnVoltarMenu;

    private ResultadoController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        inicializarComponentes();
        carregarResultado();
        configurarBotoes();
    }

    private void inicializarComponentes() {

        txtNomeJogador = findViewById(R.id.txtNomeJogador);
        txtTotalQuestoes = findViewById(R.id.txtTotalQuestoes);
        txtAcertos = findViewById(R.id.txtAcertos);
        txtErros = findViewById(R.id.txtErros);
        txtPontuacao = findViewById(R.id.txtPontuacao);

        btnJogarNovamente = findViewById(R.id.btnJogarNovamente);
        btnVoltarMenu = findViewById(R.id.btnVoltarMenu);
    }

    private void carregarResultado() {

        String nomeJogador = getIntent().getStringExtra("nome_jogador");

        int totalQuestoes = getIntent().getIntExtra("total_questoes", 0);
        int acertos = getIntent().getIntExtra("acertos", 0);
        int pontuacao = getIntent().getIntExtra("pontuacao", 0);

        controller = new ResultadoController(
                nomeJogador,
                totalQuestoes,
                acertos,
                pontuacao
        );

        txtNomeJogador.setText(
                "Jogador: " + controller.getNomeJogador()
        );

        txtTotalQuestoes.setText(
                "Quantidade de questões: " + controller.getTotalQuestoes()
        );

        txtAcertos.setText(
                "Acertos: " + controller.getAcertos()
        );

        txtErros.setText(
                "Erros: " + controller.getErros()
        );

        txtPontuacao.setText(
                "Pontuação: " + controller.getPontuacao() + " pontos"
        );
    }

    private void configurarBotoes() {

        btnJogarNovamente.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResultadoActivity.this,
                    ConfiguracaoActivity.class
            );

            intent.putExtra(
                    "nome_jogador",
                    controller.getNomeJogador()
            );

            startActivity(intent);
            finish();
        });

        btnVoltarMenu.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResultadoActivity.this,
                    MenuActivity.class
            );

            intent.putExtra(
                    "nome_jogador",
                    controller.getNomeJogador()
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
            finish();
        });
    }
}
