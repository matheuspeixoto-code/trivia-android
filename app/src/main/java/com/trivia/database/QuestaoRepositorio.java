package com.trivia.database;

import android.content.Context;
import android.database.Cursor;

import com.trivia.model.Categoria;
import com.trivia.model.Questao;
import com.trivia.provider.TriviaProvider;

import java.util.ArrayList;
import java.util.List;

public class QuestaoRepositorio {

    private final Context context;

    public QuestaoRepositorio(Context context) {
        this.context = context.getApplicationContext();
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

    public List<Questao> buscarQuestoesOffline(
            int quantidade,
            String nomeCategoria,
            String dificuldade) {

        List<Questao> resultado = new ArrayList<>();

        if (quantidade <= 0) {
            return resultado;
        }

        StringBuilder filtro = new StringBuilder(
                "tipo = ? " +
                        "AND resposta_incorreta_1 IS NOT NULL " +
                        "AND resposta_incorreta_2 IS NOT NULL " +
                        "AND resposta_incorreta_3 IS NOT NULL"
        );

        List<String> argumentos = new ArrayList<>();
        argumentos.add("multiple");

        if (dificuldade != null
                && !dificuldade.trim().isEmpty()) {

            filtro.append(" AND dificuldade = ?");
            argumentos.add(dificuldade);
        }

        if (nomeCategoria != null
                && !nomeCategoria.trim().isEmpty()
                && !nomeCategoria.equalsIgnoreCase(
                "Todas as categorias")) {

            filtro.append(
                    " AND categoria_id IN " +
                            "(SELECT id FROM categoria WHERE nome = ?)"
            );

            argumentos.add(nomeCategoria);
        }

        try (Cursor cursor = context.getContentResolver().query(
                TriviaProvider.URI_QUESTOES,
                null,
                filtro.toString(),
                argumentos.toArray(new String[0]),
                "RANDOM()"
        )) {

            if (cursor == null) {
                return resultado;
            }

            while (cursor.moveToNext()
                    && resultado.size() < quantidade) {

                String enunciado = cursor.getString(
                        cursor.getColumnIndexOrThrow("enunciado")
                );

                String nivel = cursor.getString(
                        cursor.getColumnIndexOrThrow("dificuldade")
                );

                String correta = cursor.getString(
                        cursor.getColumnIndexOrThrow("resposta_correta")
                );

                List<String> incorretas = new ArrayList<>();

                incorretas.add(cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "resposta_incorreta_1")
                ));

                incorretas.add(cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "resposta_incorreta_2")
                ));

                incorretas.add(cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "resposta_incorreta_3")
                ));

                int categoriaId = cursor.getInt(
                        cursor.getColumnIndexOrThrow("categoria_id")
                );

                String categoriaNome =
                        obterNomeCategoria(categoriaId);

                resultado.add(new Questao(
                        categoriaNome,
                        nivel,
                        enunciado,
                        correta,
                        incorretas
                ));
            }
        }

        return resultado;
    }

    private String obterNomeCategoria(int categoriaId) {

        try (Cursor cursor = context.getContentResolver().query(
                TriviaProvider.URI_CATEGORIAS,
                new String[]{"nome"},
                "id = ?",
                new String[]{String.valueOf(categoriaId)},
                null
        )) {

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        }

        return "";
    }

    public List<Categoria> listarCategoriasOffline() {

        List<Categoria> categorias = new ArrayList<>();

        categorias.add(
                new Categoria(0, "Todas as categorias")
        );

        try (Cursor cursor = context.getContentResolver().query(
                TriviaProvider.URI_CATEGORIAS,
                new String[]{"id", "nome"},
                null,
                null,
                "nome ASC"
        )) {

            if (cursor != null) {

                while (cursor.moveToNext()) {

                    int id = cursor.getInt(
                            cursor.getColumnIndexOrThrow("id")
                    );

                    String nome = cursor.getString(
                            cursor.getColumnIndexOrThrow("nome")
                    );

                    categorias.add(new Categoria(id, nome));
                }
            }
        }

        return categorias;
    }
}
