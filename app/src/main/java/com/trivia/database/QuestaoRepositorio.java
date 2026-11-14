
package com.trivia.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

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

    // Método original: insere uma questão no SQLite
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

        context.getContentResolver().insert(
                TriviaProvider.URI_QUESTOES,
                valores
        );
    }

    // Método original: consulta todas as questões
    public Cursor consultarQuestoes() {

        return context.getContentResolver().query(
                TriviaProvider.URI_QUESTOES,
                null,
                null,
                null,
                null
        );
    }

    // Busca uma categoria existente ou a cadastra
    private int garantirCategoria(String nome) {

        if (nome == null || nome.trim().isEmpty()) {
            return -1;
        }

        nome = nome.trim();

        try (Cursor cursor = context.getContentResolver().query(
                TriviaProvider.URI_CATEGORIAS,
                new String[]{"id"},
                "nome = ?",
                new String[]{nome},
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        ContentValues valores = new ContentValues();
        valores.put("nome", nome);

        Uri uri = context.getContentResolver().insert(
                TriviaProvider.URI_CATEGORIAS,
                valores
        );

        if (uri == null) {
            return -1;
        }

        return Integer.parseInt(uri.getLastPathSegment());
    }

    // Salva as perguntas recebidas da API para uso offline
    public void salvarQuestoesParaOffline(List<Questao> questoes) {

        if (questoes == null || questoes.isEmpty()) {
            return;
        }

        for (Questao questao : questoes) {

            if (questao == null
                    || questao.getEnunciado() == null
                    || questao.getRespostaCorreta() == null
                    || questao.getRespostasIncorretas() == null
                    || questao.getRespostasIncorretas().size() != 3) {
                continue;
            }

            int categoriaId = garantirCategoria(
                    questao.getCategoria()
            );

            if (categoriaId <= 0) {
                continue;
            }

            // Impede inserir novamente a mesma pergunta
            boolean jaExiste = false;

            try (Cursor cursor = context.getContentResolver().query(
                    TriviaProvider.URI_QUESTOES,
                    new String[]{"id"},
                    "enunciado = ? AND resposta_correta = ? " +
                            "AND categoria_id = ?",
                    new String[]{
                            questao.getEnunciado(),
                            questao.getRespostaCorreta(),
                            String.valueOf(categoriaId)
                    },
                    null
            )) {
                jaExiste = cursor != null && cursor.moveToFirst();
            }

            if (jaExiste) {
                continue;
            }

            List<String> incorretas =
                    questao.getRespostasIncorretas();

            inserirQuestao(
                    questao.getEnunciado(),
                    questao.getDificuldade(),
                    "multiple",
                    categoriaId,
                    questao.getRespostaCorreta(),
                    incorretas.get(0),
                    incorretas.get(1),
                    incorretas.get(2)
            );
        }
    }

    // Recupera perguntas armazenadas para uma partida offline
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
                "RANDOM() LIMIT " + quantidade
        )) {

            if (cursor == null) {
                return resultado;
            }

            while (cursor.moveToNext()) {

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
                        cursor.getColumnIndexOrThrow("resposta_incorreta_1")
                ));

                incorretas.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("resposta_incorreta_2")
                ));

                incorretas.add(cursor.getString(
                        cursor.getColumnIndexOrThrow("resposta_incorreta_3")
                ));

                int categoriaId = cursor.getInt(
                        cursor.getColumnIndexOrThrow("categoria_id")
                );

                String categoriaNome = obterNomeCategoria(categoriaId);

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

    // Recupera o nome de uma categoria armazenada
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

    // Recupera categorias disponíveis no banco local
    public List<Categoria> listarCategoriasOffline() {

        List<Categoria> categorias = new ArrayList<>();

        categorias.add(new Categoria(0, "Todas as categorias"));

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
