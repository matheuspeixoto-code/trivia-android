package com.trivia.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.trivia.database.seed.Seed;

public class BancoDados extends SQLiteOpenHelper {


    public static final String TABELA_QUESTAO = "questao";
    public static final String TABELA_CATEGORIA = "categoria";
    public static final String TABELA_JOGADOR = "jogador";
    public static final String TABELA_PARTIDA = "partida";
    private static final String NOME_BANCO = "trivia.db";
    private static final int VERSAO_BANCO = 3;

    public BancoDados(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE categoria (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nome TEXT NOT NULL UNIQUE" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE questao (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "enunciado TEXT NOT NULL, " +
                        "dificuldade TEXT, " +
                        "tipo TEXT, " +
                        "categoria_id INTEGER, " +
                        "resposta_correta TEXT NOT NULL, " +
                        "resposta_incorreta_1 TEXT, " +
                        "resposta_incorreta_2 TEXT, " +
                        "resposta_incorreta_3 TEXT, " +
                        "FOREIGN KEY (categoria_id) REFERENCES categoria(id)" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE jogador (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nome TEXT NOT NULL" +
                        ")"
        );

        db.execSQL(
                "CREATE TABLE partida (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "jogador_id INTEGER NOT NULL, " +
                        "data TEXT, " +
                        "quantidade_questoes INTEGER, " +
                        "acertos INTEGER, " +
                        "pontuacao INTEGER, " +
                        "FOREIGN KEY (jogador_id) REFERENCES jogador(id)" +
                        ")"
        );

        Seed.inserir(db);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        Seed.inserir(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        db.setForeignKeyConstraintsEnabled(true);
    }
}
