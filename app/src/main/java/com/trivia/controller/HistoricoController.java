package com.trivia.controller;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.trivia.database.PartidaRepositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoricoController {

    private static final String TAG = "HistoricoController";

    private final Context context;
    private final Handler handler;
    private final ExecutorService executor;

    private volatile boolean encerrado = false;

    public HistoricoController(Context context) {

        this.context = context.getApplicationContext();

        this.handler = new Handler(Looper.getMainLooper());

        this.executor = Executors.newSingleThreadExecutor();
    }

    public interface HistoricoCallback {

        void onSucesso(List<String> partidas);

        void onErro();
    }

    public void carregarHistorico(
            String nomeJogador,
            HistoricoCallback callback
    ) {

        executor.execute(() -> {

            try {

                PartidaRepositorio repositorio =
                        new PartidaRepositorio(context);

                List<String> partidas = new ArrayList<>();

                try (Cursor cursor =
                             repositorio.listarHistorico(nomeJogador)) {

                    if (cursor == null) {
                        notificarErro(callback);
                        return;
                    }

                    while (cursor.moveToNext()) {

                        String nome = cursor.getString(
                                cursor.getColumnIndexOrThrow("nome_jogador")
                        );

                        String data = cursor.getString(
                                cursor.getColumnIndexOrThrow("data")
                        );

                        int quantidade = cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "quantidade_questoes"
                                )
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
                }

                handler.post(() -> {

                    if (!encerrado) {
                        callback.onSucesso(partidas);
                    }
                });

            } catch (Exception e) {

                Log.e(TAG, "Erro ao carregar histórico.", e);

                notificarErro(callback);
            }
        });
    }

    private void notificarErro(HistoricoCallback callback) {

        handler.post(() -> {

            if (!encerrado) {
                callback.onErro();
            }
        });
    }

    public void encerrar() {

        encerrado = true;

        executor.shutdownNow();

        handler.removeCallbacksAndMessages(null);
    }
}
