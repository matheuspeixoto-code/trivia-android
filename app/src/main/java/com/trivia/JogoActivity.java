package com.trivia;

import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.trivia.model.Partida;
import com.trivia.model.Questao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JogoActivity extends AppCompatActivity {

    private TextView txtProgresso;
    private TextView txtEnunciado;
    private TextView txtResultado;
    private RadioGroup grupoAlternativas;
    private Button btnAcao;

    private Partida partida;
    private int indiceAtual = 0;
    private int acertos = 0;

    private boolean respostaConfirmada = false;
    private String respostaSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogo);

        txtProgresso = findViewById(R.id.txtProgresso);
        txtEnunciado = findViewById(R.id.txtEnunciado);
        txtResultado = findViewById(R.id.txtResultado);
        grupoAlternativas = findViewById(R.id.grupoAlternativas);
        btnAcao = findViewById(R.id.btnAcao);

        // Recupera a partida enviada pela ConfiguracaoActivity.
        partida = (Partida) getIntent().getSerializableExtra("partida");

        if (partida == null || partida.getQuestoes() == null
                || partida.getQuestoes().isEmpty()) {
            Toast.makeText(this, "Não foi possível iniciar a partida.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        mostrarQuestao();

        btnAcao.setOnClickListener(v -> executarAcao());
    }

    private void mostrarQuestao() {
        Questao questao = partida.getQuestoes().get(indiceAtual);

        txtProgresso.setText(
                "Questão " + (indiceAtual + 1) +
                        " de " + partida.getQuestoes().size()
        );

        // Converte entidades HTML da API para caracteres normais.
        txtEnunciado.setText(
                Html.fromHtml(questao.getEnunciado(), Html.FROM_HTML_MODE_LEGACY)
        );

        grupoAlternativas.removeAllViews();
        txtResultado.setText("");

        respostaConfirmada = false;
        respostaSelecionada = null;
        btnAcao.setText("Confirmar resposta");

        // Junta a resposta correta com as incorretas.
        List<String> alternativas = new ArrayList<>();
        alternativas.add(questao.getRespostaCorreta());

        if (questao.getRespostasIncorretas() != null) {
            alternativas.addAll(questao.getRespostasIncorretas());
        }

        // Embaralha para a resposta correta não ficar sempre na mesma posição.
        Collections.shuffle(alternativas);

        for (String alternativa : alternativas) {
            RadioButton radioButton = new RadioButton(this);

            radioButton.setText(
                    Html.fromHtml(alternativa, Html.FROM_HTML_MODE_LEGACY)
            );

            radioButton.setTextSize(16);
            radioButton.setTag(alternativa);

            grupoAlternativas.addView(radioButton);
        }
    }

    private void executarAcao() {

        // Primeiro clique: verifica a resposta.
        if (!respostaConfirmada) {

            int idSelecionado = grupoAlternativas.getCheckedRadioButtonId();

            if (idSelecionado == -1) {
                Toast.makeText(this, "Selecione uma alternativa.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selecionado = findViewById(idSelecionado);
            respostaSelecionada = (String) selecionado.getTag();

            verificarResposta();

            return;
        }

        // Segundo clique: avança para a próxima questão.
        indiceAtual++;

        if (indiceAtual < partida.getQuestoes().size()) {
            mostrarQuestao();
        } else {
            finalizarPartida();
        }
    }

    private void verificarResposta() {
        Questao questao = partida.getQuestoes().get(indiceAtual);

        boolean acertou = respostaSelecionada.equals(
                questao.getRespostaCorreta()
        );

        if (acertou) {
            acertos++;
            txtResultado.setText("Resposta correta!");
        } else {
            txtResultado.setText(
                    "Resposta incorreta. Correta: " +
                            Html.fromHtml(
                                    questao.getRespostaCorreta(),
                                    Html.FROM_HTML_MODE_LEGACY
                            )
            );
        }

        respostaConfirmada = true;
        btnAcao.setText("Próxima questão");

        // Impede trocar a alternativa depois de confirmar.
        for (int i = 0; i < grupoAlternativas.getChildCount(); i++) {
            grupoAlternativas.getChildAt(i).setEnabled(false);
        }
    }

    private void finalizarPartida() {
        new AlertDialog.Builder(this)
                .setTitle("Partida finalizada")
                .setMessage(
                        "Você acertou " + acertos + " de " +
                                partida.getQuestoes().size() + " questões."
                )
                .setCancelable(false)
                .setPositiveButton("Voltar ao menu", (dialog, which) -> {
                    finish();
                })
                .show();
    }
}