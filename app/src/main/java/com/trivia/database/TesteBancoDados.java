
package com.trivia.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TesteBancoDados {

    private static final String TAG = "TesteBancoDados";

    private static final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    public static void executar(Context context) {

        Context contexto = context.getApplicationContext();

        executor.execute(() -> {

            try {
                QuestaoRepositorio repositorio =
                        new QuestaoRepositorio(contexto);


                repositorio.inserirQuestao(
                        "Qual linguagem é utilizada no Android?",
                        "easy",
                        "multiple",
                        0,
                        "Java",
                        "Python",
                        "C++",
                        "Ruby"
                );

                Log.i(TAG, "Questão enviada para inserção.");


                try (Cursor cursor = repositorio.consultarQuestoes()) {

                    if (cursor != null && cursor.moveToLast()) {

                        int indiceEnunciado =
                                cursor.getColumnIndexOrThrow("enunciado");

                        int indiceResposta =
                                cursor.getColumnIndexOrThrow("resposta_correta");

                        String enunciado =
                                cursor.getString(indiceEnunciado);

                        String resposta =
                                cursor.getString(indiceResposta);

                        // 3. Exibir o resultado
                        Log.i(TAG, "Questão encontrada: " + enunciado);
                        Log.i(TAG, "Resposta correta: " + resposta);

                    } else {
                        Log.w(TAG, "Nenhuma questão encontrada.");
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Erro ao testar o banco de dados.", e);
            }
        });
    }
}
