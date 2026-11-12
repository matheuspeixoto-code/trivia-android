
package com.trivia.database;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.trivia.provider.TriviaProvider;

public class PartidaRepositorio {

    private final Context context;

    public PartidaRepositorio(Context context) {
        this.context = context;
    }

    public boolean salvar(
            long jogadorId,
            int quantidadeQuestoes,
            int acertos,
            int pontuacao) {

        ContentValues valores = new ContentValues();

        valores.put("jogador_id", jogadorId);

        valores.put(
                "data",
                new java.text.SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        java.util.Locale.getDefault()
                ).format(new java.util.Date())
        );

        valores.put("quantidade_questoes", quantidadeQuestoes);
        valores.put("acertos", acertos);
        valores.put("pontuacao", pontuacao);

        Uri uri = context.getContentResolver().insert(
                TriviaProvider.URI_PARTIDAS,
                valores
        );

        return uri != null;
    }
}
