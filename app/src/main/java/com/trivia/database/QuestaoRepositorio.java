
package com.trivia.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.trivia.provider.TriviaProvider;

public class QuestaoRepositorio {

    private final Context context;

    public QuestaoRepositorio(Context context) {
        this.context = context;
    }

    public void inserirQuestao(
            String enunciado,
            String dificuldade,
            String tipo,
            int categoriaId,
            String respostaCorreta,
            String respostaIncorreta1,
            String respostaIncorreta2,
            String respostaIncorreta3) {

        ContentValues valores = new ContentValues();

        valores.put("enunciado", enunciado);
        valores.put("dificuldade", dificuldade);
        valores.put("tipo", tipo);
        if (categoriaId > 0) {
            valores.put("categoria_id", categoriaId);
        } else {
            valores.putNull("categoria_id");
        }
        valores.put("resposta_correta", respostaCorreta);
        valores.put("resposta_incorreta_1", respostaIncorreta1);
        valores.put("resposta_incorreta_2", respostaIncorreta2);
        valores.put("resposta_incorreta_3", respostaIncorreta3);

        Uri uri = context.getContentResolver().insert(
                TriviaProvider.URI_QUESTOES,
                valores
        );
    }

    public Cursor consultarQuestoes() {

        return context.getContentResolver().query(
                TriviaProvider.URI_QUESTOES,
                null,
                null,
                null,
                null
        );
    }
}
