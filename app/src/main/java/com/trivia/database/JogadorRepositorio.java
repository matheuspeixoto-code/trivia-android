
package com.trivia.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.trivia.provider.TriviaProvider;

public class JogadorRepositorio {

    private final Context context;

    public JogadorRepositorio(Context context) {
        this.context = context;
    }

    public long obterOuCadastrar(String nome) {

        // Consulta se o jogador já existe.
        Cursor cursor = context.getContentResolver().query(
                TriviaProvider.URI_JOGADORES,
                new String[]{"id"},
                "nome = ?",
                new String[]{nome},
                null
        );

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getLong(
                            cursor.getColumnIndexOrThrow("id")
                    );
                }
            } finally {
                cursor.close();
            }
        }

        // Se não existe, cadastra o jogador.
        ContentValues valores = new ContentValues();
        valores.put("nome", nome);

        Uri uri = context.getContentResolver().insert(
                TriviaProvider.URI_JOGADORES,
                valores
        );

        if (uri == null) {
            return -1;
        }

        // Recupera o ID gerado pelo SQLite.
        return android.content.ContentUris.parseId(uri);
    }
}
