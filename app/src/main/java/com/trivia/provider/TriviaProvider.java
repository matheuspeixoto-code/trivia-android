
package com.trivia.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.trivia.database.BancoDados;

public class TriviaProvider extends ContentProvider {

    public static final String AUTHORITY = "com.trivia.provider";

    // URIs das tabelas
    public static final Uri URI_QUESTOES =
            Uri.parse("content://" + AUTHORITY + "/questao");

    public static final Uri URI_CATEGORIAS =
            Uri.parse("content://" + AUTHORITY + "/categoria");

    public static final Uri URI_JOGADORES =
            Uri.parse("content://" + AUTHORITY + "/jogador");

    public static final Uri URI_PARTIDAS =
            Uri.parse("content://" + AUTHORITY + "/partida");

    public static final Uri URI_HISTORICO =
            Uri.parse("content://" + AUTHORITY + "/historico");

    // Códigos para identificar cada tabela
    private static final int QUESTOES = 1;
    private static final int JOGADORES = 2;
    private static final int PARTIDAS = 3;
    private static final int HISTORICO = 4;
    private static final int CATEGORIAS = 5;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "questao", QUESTOES);
        uriMatcher.addURI(AUTHORITY, "jogador", JOGADORES);
        uriMatcher.addURI(AUTHORITY, "partida", PARTIDAS);
        uriMatcher.addURI(AUTHORITY, "historico", HISTORICO);
        uriMatcher.addURI(AUTHORITY, "categoria", CATEGORIAS);
    }

    private BancoDados bancoDados;

    @Override
    public boolean onCreate() {
        bancoDados = new BancoDados(getContext());
        return true;
    }

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        SQLiteDatabase db = bancoDados.getReadableDatabase();

        String tabela;

        switch (uriMatcher.match(uri)) {

            case QUESTOES:
                tabela = BancoDados.TABELA_QUESTAO;
                break;

            case CATEGORIAS:
                tabela = BancoDados.TABELA_CATEGORIA;
                break;

            case JOGADORES:
                tabela = BancoDados.TABELA_JOGADOR;
                break;

            case PARTIDAS:
                tabela = BancoDados.TABELA_PARTIDA;
                break;

            case HISTORICO:

                String sql =
                        "SELECT " +
                        "partida.id AS partida_id, " +
                        "jogador.nome AS nome_jogador, " +
                        "partida.data, " +
                        "partida.quantidade_questoes, " +
                        "partida.acertos, " +
                        "partida.pontuacao " +
                        "FROM partida " +
                        "INNER JOIN jogador " +
                        "ON partida.jogador_id = jogador.id " +
                        "WHERE jogador.nome = ? " +
                        "ORDER BY partida.id DESC";

                return db.rawQuery(sql, selectionArgs);

            default:
                throw new IllegalArgumentException(
                        "URI desconhecida: " + uri
                );
        }

        return db.query(
                tabela,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = bancoDados.getWritableDatabase();

        String tabela;

        switch (uriMatcher.match(uri)) {

            case QUESTOES:
                tabela = BancoDados.TABELA_QUESTAO;
                break;

            case CATEGORIAS:
                tabela = BancoDados.TABELA_CATEGORIA;
                break;

            case JOGADORES:
                tabela = BancoDados.TABELA_JOGADOR;
                break;

            case PARTIDAS:
                tabela = BancoDados.TABELA_PARTIDA;
                break;

            default:
                throw new IllegalArgumentException(
                        "URI desconhecida: " + uri
                );
        }

        long id = db.insert(tabela, null, values);

        if (id == -1) {
            return null;
        }

        return Uri.withAppendedPath(
                uri,
                String.valueOf(id)
        );
    }

    @Override
    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {

        return 0;
    }

    @Override
    public int delete(
            Uri uri,
            String selection,
            String[] selectionArgs) {

        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}
