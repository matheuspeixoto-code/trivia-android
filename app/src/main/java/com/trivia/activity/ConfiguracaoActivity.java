package com.trivia.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.trivia.R;
import com.trivia.broadcast.ConexaoReceiver;
import com.trivia.controller.ConfiguracaoController;
import com.trivia.model.Categoria;
import com.trivia.model.Partida;
import com.trivia.utils.ConfiguracaoUtils;

import java.util.List;

public class ConfiguracaoActivity extends AppCompatActivity {

    private static final String TAG = "ConfiguracaoActivity";

    private LinearLayout layoutCarregando;
    private LinearLayout layoutConfiguracao;
    private Spinner spDificuldade;
    private Spinner spCategoria;
    private EditText edtQuantidade;
    private SeekBar seekTempo;
    private TextView txtTempo;
    private Button btnIniciarPartida;

    private ConfiguracaoController controller;

    private final ConexaoReceiver conexaoReceiver =
            new ConexaoReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        controller = new ConfiguracaoController(this);

        layoutCarregando = findViewById(R.id.layoutCarregando);
        layoutConfiguracao = findViewById(R.id.layoutConfiguracao);

        spDificuldade = findViewById(R.id.spDificuldade);
        spCategoria = findViewById(R.id.spCategoria);

        edtQuantidade = findViewById(R.id.edtQuantidade);
        seekTempo = findViewById(R.id.seekTempo);
        txtTempo = findViewById(R.id.txtTempo);

        btnIniciarPartida = findViewById(R.id.btnIniciarPartida);

        configurarTempo();
        configurarDificuldade();
        carregarCategorias();

        btnIniciarPartida.setOnClickListener(
                v -> obterConfiguracao()
        );
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filtro = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION
        );

        registerReceiver(conexaoReceiver, filtro);

        Log.d(TAG, "onStart() chamado");
    }

    @Override
    protected void onStop() {
        unregisterReceiver(conexaoReceiver);

        super.onStop();

        Log.d(TAG, "onStop() chamado");
    }

    @Override
    protected void onDestroy() {
        if (controller != null) {
            controller.encerrar();
        }

        super.onDestroy();
    }

    private void configurarDificuldade() {

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

    private void configurarTempo() {

        seekTempo.setProgress(20);

        txtTempo.setText(
                (seekTempo.getProgress() + 10) + " segundos"
        );

        seekTempo.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        txtTempo.setText(
                                (progress + 10) + " segundos"
                        );
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );
    }

    private void carregarCategorias() {

        controller.carregarCategorias(
                (categorias, locais) -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    ArrayAdapter<Categoria> adapter =
                            new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_spinner_item,
                                    categorias
                            );

                    adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                    );

                    spCategoria.setAdapter(adapter);

                    layoutCarregando.setVisibility(View.GONE);
                    layoutConfiguracao.setVisibility(View.VISIBLE);

                    if (locais) {
                        String mensagem = categorias.size() > 1
                                ? "Modo offline: categorias salvas carregadas."
                                : "Sem internet. Use perguntas salvas, se houver.";

                        Toast.makeText(
                                this,
                                mensagem,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void obterConfiguracao() {

        Categoria categoria =
                (Categoria) spCategoria.getSelectedItem();

        String dificuldade =
                ConfiguracaoUtils.obterDificuldade(
                        spDificuldade.getSelectedItemPosition()
                );

        String textoQuantidade =
                edtQuantidade.getText().toString();

        int tempo = seekTempo.getProgress() + 10;

        btnIniciarPartida.setEnabled(false);

        controller.iniciarPartida(
                getIntent().getStringExtra("nome_jogador"),
                categoria,
                textoQuantidade,
                dificuldade,
                tempo,
                new ConfiguracaoController.PartidaCallback() {

                    @Override
                    public void onErroValidacao(String mensagem) {
                        btnIniciarPartida.setEnabled(true);
                        edtQuantidade.setError(mensagem);
                    }

                    @Override
                    public void onErro(String mensagem) {
                        btnIniciarPartida.setEnabled(true);

                        Toast.makeText(
                                ConfiguracaoActivity.this,
                                mensagem,
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onPartidaPreparada(
                            Partida partida,
                            boolean offline) {

                        btnIniciarPartida.setEnabled(true);

                        Intent intent = new Intent(
                                ConfiguracaoActivity.this,
                                JogoActivity.class
                        );

                        intent.putExtra("partida", partida);

                        startActivity(intent);

                        Toast.makeText(
                                ConfiguracaoActivity.this,
                                offline
                                        ? "Partida offline iniciada!"
                                        : "Questões carregadas com sucesso!",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }
}
