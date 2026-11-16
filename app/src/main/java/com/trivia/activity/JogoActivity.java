package com.trivia.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import com.trivia.R;
import com.trivia.controller.JogoController;
import com.trivia.model.Partida;
import com.trivia.service.PartidaService;

import java.util.List;

public class JogoActivity extends AppCompatActivity {

    private static final String TAG = "JogoActivity";

    private TextView txtProgresso;
    private TextView txtEnunciado;
    private TextView txtResultado;
    private RadioGroup grupoAlternativas;
    private Button btnAcao;
    private LinearProgressIndicator progressoTempo;

    private Partida partida;
    private JogoController controller;

    private CountDownTimer temporizador;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean partidaSalva = false;

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
        progressoTempo = findViewById(R.id.progressoTempo);

        partida = (Partida) getIntent().getSerializableExtra("partida");

        if (partida == null || partida.getQuestoes() == null
                || partida.getQuestoes().isEmpty()) {

            Toast.makeText(
                    this,
                    "Não foi possível iniciar a partida.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        controller = new JogoController(partida);

        mostrarQuestao();

        btnAcao.setOnClickListener(v -> executarAcao());
    }

    private void mostrarQuestao() {

        txtProgresso.setText(
                "Questão " + (controller.getIndiceAtual() + 1) +
                        " de " + controller.getQuantidadeQuestoes()
        );

        txtEnunciado.setText(
                Html.fromHtml(
                        controller.obterQuestaoAtual().getEnunciado(),
                        Html.FROM_HTML_MODE_LEGACY
                )
        );

        grupoAlternativas.removeAllViews();
        txtResultado.setText("");

        respostaSelecionada = null;

        btnAcao.setText("Confirmar resposta");
        btnAcao.setEnabled(true);

        List<String> alternativas =
                controller.obterAlternativasEmbaralhadas();

        for (int i = 0; i < alternativas.size(); i++) {
            grupoAlternativas.addView(
                    criarAlternativa(alternativas.get(i), i)
            );
        }

        iniciarTemporizador();
    }


    private RadioButton criarAlternativa(String alternativa, int posicao) {

        RadioButton rb = new RadioButton(this);

        rb.setId(View.generateViewId());

        SpannableStringBuilder texto = new SpannableStringBuilder();

        String letra = String.valueOf((char) ('A' + posicao));

        texto.append(letra);

        texto.setSpan(
                new StyleSpan(Typeface.BOLD),
                0,
                letra.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        texto.setSpan(
                new ForegroundColorSpan(
                        ContextCompat.getColor(this, R.color.trivia_primary)
                ),
                0,
                letra.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        texto.append("     ");

        texto.append(
                Html.fromHtml(
                        alternativa,
                        Html.FROM_HTML_MODE_LEGACY
                )
        );

        rb.setText(texto);
        rb.setTextSize(16);

        rb.setTextColor(
                ContextCompat.getColor(this, R.color.trivia_ink)
        );

        rb.setTag(alternativa);

        rb.setButtonDrawable((android.graphics.drawable.Drawable) null);
        rb.setBackgroundResource(R.drawable.bg_alternativa);
        rb.setGravity(Gravity.CENTER_VERTICAL);
        rb.setMinHeight(dp(64));
        rb.setPadding(dp(20), dp(14), dp(20), dp(14));

        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
        );

        lp.bottomMargin = dp(12);

        rb.setLayoutParams(lp);

        return rb;
    }


    private void destacarAlternativas(
            String selecionada,
            String correta
    ) {

        for (int i = 0; i < grupoAlternativas.getChildCount(); i++) {

            RadioButton rb =
                    (RadioButton) grupoAlternativas.getChildAt(i);

            String valor = (String) rb.getTag();

            if (valor.equals(correta)) {

                rb.setBackgroundResource(
                        R.drawable.bg_alternativa_correta
                );

            } else if (valor.equals(selecionada)) {

                rb.setBackgroundResource(
                        R.drawable.bg_alternativa_errada
                );

            } else {

                rb.setAlpha(0.55f);
            }
        }
    }

    private int dp(int valor) {
        return Math.round(
                valor * getResources().getDisplayMetrics().density
        );
    }


    private void executarAcao() {


        Log.d(TAG, "executarAcao chamada");
        Log.d(TAG, "Resposta confirmada: " +
                controller.isRespostaConfirmada());
        Log.d(TAG, "Alternativa selecionada: " +
                grupoAlternativas.getCheckedRadioButtonId());



        if (!controller.isRespostaConfirmada()) {

            int idSelecionado =
                    grupoAlternativas.getCheckedRadioButtonId();

            if (idSelecionado == -1) {

                Toast.makeText(
                        this,
                        "Selecione uma alternativa.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            View viewSelecionada =
                    grupoAlternativas.findViewById(idSelecionado);

            if (!(viewSelecionada instanceof RadioButton)) {
                Log.e(TAG, "Não foi possível localizar a alternativa selecionada.");

                Toast.makeText(
                        this,
                        "Erro ao identificar a alternativa. Selecione novamente.",
                        Toast.LENGTH_SHORT
                ).show();

                grupoAlternativas.clearCheck();
                return;
            }

            RadioButton selecionado = (RadioButton) viewSelecionada;

            respostaSelecionada = (String) selecionado.getTag();

            verificarResposta();

            return;
        }

        avancarQuestao();
    }

    private void verificarResposta() {

        cancelarTemporizador();

        JogoController.ResultadoResposta resultado =
                controller.verificarResposta(respostaSelecionada);

        if (resultado == null) {
            return;
        }

        if (resultado.isAcertou()) {

            txtResultado.setText("Resposta correta!");

        } else {

            txtResultado.setText(
                    "Resposta incorreta. Correta: " +
                            Html.fromHtml(
                                    resultado.getRespostaCorreta(),
                                    Html.FROM_HTML_MODE_LEGACY
                            )
            );
        }

        txtResultado.setTextColor(
                ContextCompat.getColor(
                        this,
                        resultado.isAcertou()
                                ? R.color.trivia_correct
                                : R.color.trivia_wrong
                )
        );

        destacarAlternativas(
                respostaSelecionada,
                resultado.getRespostaCorreta()
        );

        btnAcao.setText("Próxima questão");

        desabilitarAlternativas();
    }

    private void desabilitarAlternativas() {

        for (int i = 0; i < grupoAlternativas.getChildCount(); i++) {
            grupoAlternativas.getChildAt(i).setEnabled(false);
        }
    }

    private void avancarQuestao() {

        cancelarTemporizador();

        boolean existeProximaQuestao = controller.avancarQuestao();

        if (existeProximaQuestao) {

            mostrarQuestao();

        } else {

            finalizarPartida();
        }
    }


    private void iniciarTemporizador() {

        long tempo = partida.getTempoPorQuestao() * 1000L;

        progressoTempo.setMax((int) tempo);
        progressoTempo.setProgressCompat((int) tempo, false);

        temporizador = new CountDownTimer(tempo, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                long segundos = millisUntilFinished / 1000;

                progressoTempo.setProgressCompat(
                        (int) millisUntilFinished,
                        true
                );

                txtProgresso.setText(
                        "Questão " + (controller.getIndiceAtual() + 1) +
                                " de " + controller.getQuantidadeQuestoes() +
                                " | Tempo: " + segundos + "s"
                );
            }

            @Override
            public void onFinish() {

                temporizador = null;

                progressoTempo.setProgressCompat(0, true);

                tempoEsgotado();
            }

        }.start();
    }

    private void cancelarTemporizador() {

        if (temporizador != null) {

            temporizador.cancel();
            temporizador = null;
        }
    }

    private void tempoEsgotado() {

        String respostaCorreta = controller.registrarTempoEsgotado();

        if (respostaCorreta == null) {
            return;
        }

        txtResultado.setText(
                "Tempo esgotado! Você não respondeu a questão."
        );

        txtResultado.setTextColor(
                ContextCompat.getColor(
                        this,
                        R.color.trivia_wrong
                )
        );

        destacarAlternativas(null, respostaCorreta);

        desabilitarAlternativas();

        btnAcao.setEnabled(false);

        handler.postDelayed(() -> {

            btnAcao.setEnabled(true);

            avancarQuestao();

        }, 1500);
    }


    private void finalizarPartida() {

        if (partidaSalva) {
            return;
        }

        partidaSalva = true;

        btnAcao.setEnabled(false);

        controller.finalizarPartida();

        ResultReceiver receiver = new ResultReceiver(
                new Handler(Looper.getMainLooper())
        ) {

            @Override
            protected void onReceiveResult(
                    int resultCode,
                    Bundle resultData
            ) {

                boolean salvo = resultData != null
                        && resultData.getBoolean("salvo");

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
                        controller.getQuantidadeQuestoes()
                );

                intent.putExtra(
                        "acertos",
                        controller.getAcertos()
                );

                intent.putExtra(
                        "pontuacao",
                        partida.getPontuacao()
                );

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
        super.onDestroy();

        cancelarTemporizador();

        handler.removeCallbacksAndMessages(null);

        Log.d(TAG, "onDestroy");
    }
}
