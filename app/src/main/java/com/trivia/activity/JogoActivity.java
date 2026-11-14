package com.trivia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.trivia.R;
import com.trivia.model.Partida;
import com.trivia.model.Questao;
import com.trivia.service.PartidaService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

public class JogoActivity extends AppCompatActivity {

    private TextView txtProgresso;
    private TextView txtEnunciado;
    private TextView txtResultado;
    private RadioGroup grupoAlternativas;
    private Button btnAcao;

    private Partida partida;
    private int indiceAtual = 0;
    private int acertos = 0;

    private CountDownTimer temporizador;
    private Handler handler = new Handler(Looper.getMainLooper());


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

        txtEnunciado.setText(
                Html.fromHtml(questao.getEnunciado(), Html.FROM_HTML_MODE_LEGACY)
        );

        grupoAlternativas.removeAllViews();
        txtResultado.setText("");

        respostaConfirmada = false;
        respostaSelecionada = null;
        btnAcao.setText("Confirmar resposta");

        List<String> alternativas = new ArrayList<>();
        alternativas.add(questao.getRespostaCorreta());

        if (questao.getRespostasIncorretas() != null) {
            alternativas.addAll(questao.getRespostasIncorretas());
        }

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

        btnAcao.setEnabled(true);

        iniciarTemporizador();
    }

    private void executarAcao() {


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


        avancarQuestao();
    }

    private void verificarResposta() {

        if (temporizador != null) {
            temporizador.cancel();
            temporizador = null;
        }

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


        partida.setQuantidadeAcertos(acertos);
        partida.setQuantidadeErros(
                partida.getQuestoes().size() - acertos
        );
        partida.setPontuacao(acertos * 100);


        ResultReceiver receiver = new ResultReceiver(
                new Handler(Looper.getMainLooper())
        ) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {

                boolean salvo = resultData.getBoolean("salvo");

                if (!salvo) {
                    Toast.makeText(
                            JogoActivity.this,
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
                intent.putExtra("pontuacao", partida.getPontuacao());

                startActivity(intent);
                finish();
            }
        };


        Intent serviceIntent = new Intent(
                JogoActivity.this,
                PartidaService.class
        );

        serviceIntent.putExtra(
                PartidaService.EXTRA_PARTIDA,
                partida
        );

        serviceIntent.putExtra(
                PartidaService.EXTRA_RECEIVER,
                receiver
        );

        startService(serviceIntent);
    }


    private void iniciarTemporizador() {

        long tempo = partida.getTempoPorQuestao() * 1000L;

        temporizador = new CountDownTimer(tempo, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                long segundos = millisUntilFinished / 1000;

                txtProgresso.setText(
                        "Questão " + (indiceAtual + 1) +
                                " de " + partida.getQuestoes().size() +
                                " | Tempo: " + segundos + "s"
                );
            }

            @Override
            public void onFinish() {

                temporizador = null;

                tempoEsgotado();
            }

        }.start();
    }

    private void tempoEsgotado() {

        respostaConfirmada = true;

        txtResultado.setText(
                "Tempo esgotado! Você não respondeu a questão."
        );

        btnAcao.setEnabled(false);

        for (int i = 0; i < grupoAlternativas.getChildCount(); i++) {
            grupoAlternativas.getChildAt(i).setEnabled(false);
        }

        handler.postDelayed(() -> {
            avancarQuestao();
        }, 1500);
    }

    private void avancarQuestao() {

        indiceAtual++;

        if (indiceAtual < partida.getQuestoes().size()) {

            mostrarQuestao();

        } else {

            finalizarPartida();
        }
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
        super.onDestroy();

        if (temporizador != null) {
            temporizador.cancel();
            temporizador = null;
        }

        handler.removeCallbacksAndMessages(null);
    }


}