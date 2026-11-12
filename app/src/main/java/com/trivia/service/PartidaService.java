package com.trivia.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;

import com.trivia.database.JogadorRepositorio;
import com.trivia.database.PartidaRepositorio;
import com.trivia.model.Partida;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PartidaService extends Service {

    public static final String EXTRA_PARTIDA = "partida";
    public static final String EXTRA_RECEIVER = "receiver";

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            stopSelf(startId);
            return START_NOT_STICKY;
        }

        Partida partida = (Partida) intent.getSerializableExtra(
                EXTRA_PARTIDA
        );

        ResultReceiver receiver =
                intent.getParcelableExtra(EXTRA_RECEIVER);

        if (partida == null || partida.getJogador() == null) {
            enviarResultado(receiver, false);
            stopSelf(startId);
            return START_NOT_STICKY;
        }

        executor.execute(() -> {

            boolean salvo = false;

            try {
                JogadorRepositorio jogadorRepositorio =
                        new JogadorRepositorio(this);

                long jogadorId =
                        jogadorRepositorio.obterOuCadastrar(
                                partida.getJogador().getNome()
                        );

                if (jogadorId != -1) {

                    PartidaRepositorio partidaRepositorio =
                            new PartidaRepositorio(this);

                    salvo = partidaRepositorio.salvar(
                            jogadorId,
                            partida.getQuestoes().size(),
                            partida.getQuantidadeAcertos(),
                            partida.getPontuacao()
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            boolean resultadoSalvamento = salvo;

            enviarResultado(receiver, resultadoSalvamento);

            stopSelf(startId);
        });

        return START_NOT_STICKY;
    }

    private void enviarResultado(
            ResultReceiver receiver,
            boolean salvo
    ) {
        if (receiver != null) {

            Bundle resultado = new Bundle();

            resultado.putBoolean("salvo", salvo);

            receiver.send(
                    salvo ? Activity.RESULT_OK : Activity.RESULT_CANCELED,
                    resultado
            );
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        executor.shutdown();
        super.onDestroy();
    }
}
