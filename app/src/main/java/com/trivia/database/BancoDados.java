package com.trivia.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoDados extends SQLiteOpenHelper {


    private static final String NOME_BANCO = "trivia.db";
    private static final int VERSAO_BANCO = 1;


    public static final String TABELA_CATEGORIA = "categoria";
    public static final String TABELA_QUESTAO = "questao";

    public static final String TABELA_JOGADOR = "jogador";
    public static final String TABELA_PARTIDA = "partida";

    public BancoDados(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(
                "CREATE TABLE " + TABELA_CATEGORIA + " (" +
                        "id INTEGER PRIMARY KEY, " +
                        "nome TEXT NOT NULL" +
                        ")"
        );


        db.execSQL(
                "CREATE TABLE " + TABELA_QUESTAO + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "enunciado TEXT NOT NULL, " +
                        "dificuldade TEXT, " +
                        "tipo TEXT, " +
                        "categoria_id INTEGER, " +
                        "resposta_correta TEXT NOT NULL, " +
                        "resposta_incorreta_1 TEXT, " +
                        "resposta_incorreta_2 TEXT, " +
                        "resposta_incorreta_3 TEXT, " +
                        "FOREIGN KEY (categoria_id) REFERENCES " +
                        TABELA_CATEGORIA + "(id)" +
                        ")"
        );





        db.execSQL(
                "CREATE TABLE " + TABELA_JOGADOR + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nome TEXT NOT NULL" +
                        ")"
        );


        db.execSQL(
                "CREATE TABLE " + TABELA_PARTIDA + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "jogador_id INTEGER NOT NULL, " +
                        "data TEXT NOT NULL, " +
                        "quantidade_questoes INTEGER NOT NULL, " +
                        "acertos INTEGER NOT NULL, " +
                        "pontuacao INTEGER NOT NULL, " +
                        "FOREIGN KEY (jogador_id) REFERENCES " +
                        TABELA_JOGADOR + "(id)" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db, int versaoAntiga, int versaoNova) {


    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
