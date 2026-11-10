
package com.trivia.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.trivia.database.BancoDados;

public class TriviaProvider extends ContentProvider {

    public static final String AUTHORITY = "com.trivia.provider";

    public static final Uri URI_QUESTOES =
            Uri.parse("content://" + AUTHORITY + "/questao");

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

        return db.query(
                BancoDados.TABELA_QUESTAO,
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

        long id = db.insert(
                BancoDados.TABELA_QUESTAO,
                null,
                values
        );

        return Uri.withAppendedPath(uri, String.valueOf(id));
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
