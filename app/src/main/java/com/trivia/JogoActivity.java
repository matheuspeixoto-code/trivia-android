package com.trivia;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.trivia.database.JogadorRepositorio;
import com.trivia.database.PartidaRepositorio;
import com.trivia.model.Partida;
import com.trivia.model.Questao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JogoActivity extends AppCompatActivity {

    private TextView txtProgresso;
    private TextView txtEnunciado;
    private TextView txtResultado;
    private RadioGroup grupoAlternativas;
    private Button btnAcao;

    private Partida partida;
    private int indiceAtual = 0;
    private int acertos = 0;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private boolean partidaSalva = false;

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

        if (partidaSalva) {
            return;
        }

        partidaSalva = true;
        btnAcao.setEnabled(false);

        executor.execute(() -> {

            JogadorRepositorio jogadorRepositorio =
                    new JogadorRepositorio(this);

            long jogadorId = jogadorRepositorio.obterOuCadastrar(
                    partida.getJogador().getNome()
            );

            boolean salvo = false;

            if (jogadorId != -1) {

                PartidaRepositorio partidaRepositorio =
                        new PartidaRepositorio(this);

                salvo = partidaRepositorio.salvar(
                        jogadorId,
                        partida.getQuestoes().size(),
                        acertos,
                        acertos * 100
                );
            }

            boolean resultadoSalvamento = salvo;


            runOnUiThread(() -> {

                if (!resultadoSalvamento) {
                    Toast.makeText(
                            this,
                            "Não foi possível salvar o resultado.",
                            Toast.LENGTH_LONG
                    ).show();
                }

                Intent intent = new Intent(
                        JogoActivity.this,
                        ResultadoActivity.class
                );

                intent.putExtra(
                        "nome_jogador",
                        partida.getJogador().getNome()
                );

                intent.putExtra(
                        "total_questoes",
                        partida.getQuestoes().size()
                );

                intent.putExtra("acertos", acertos);

                intent.putExtra("pontuacao", acertos * 100);

                startActivity(intent);

                finish();
            });
        });
    }

}