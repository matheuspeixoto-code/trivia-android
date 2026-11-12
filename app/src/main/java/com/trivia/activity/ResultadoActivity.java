
package com.trivia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.trivia.R;

public class ResultadoActivity extends AppCompatActivity {

    private TextView txtNomeJogador;
    private TextView txtTotalQuestoes;
    private TextView txtAcertos;
    private TextView txtErros;
    private TextView txtPontuacao;

    private Button btnJogarNovamente;
    private Button btnVoltarMenu;

    private String nomeJogador;

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

        nomeJogador = getIntent().getStringExtra("nome_jogador");

        int totalQuestoes = getIntent().getIntExtra("total_questoes", 0);
        int acertos = getIntent().getIntExtra("acertos", 0);
        int pontuacao = getIntent().getIntExtra("pontuacao", 0);

        int erros = totalQuestoes - acertos;

        txtNomeJogador.setText("Jogador: " + nomeJogador);
        txtTotalQuestoes.setText("Quantidade de questões: " + totalQuestoes);
        txtAcertos.setText("Acertos: " + acertos);
        txtErros.setText("Erros: " + erros);
        txtPontuacao.setText("Pontuação: " + pontuacao + " pontos");
    }

    private void configurarBotoes() {

        btnJogarNovamente.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResultadoActivity.this,
                    ConfiguracaoActivity.class
            );

            intent.putExtra("nome_jogador", nomeJogador);

            startActivity(intent);
            finish();
        });

        btnVoltarMenu.setOnClickListener(v -> {

            Intent intent = new Intent(
                    ResultadoActivity.this,
                    MenuActivity.class
            );

            intent.putExtra("nome_jogador", nomeJogador);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
            );

            startActivity(intent);
            finish();
        });
    }
}
