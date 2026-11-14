package com.trivia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.trivia.R;
import com.trivia.controller.MainController;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CicloDeVida_MainActivity";

    private EditText edtNomeJogador;
    private Button btnComecar;

    private MainController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate() chamado");

        controller = new MainController();

        edtNomeJogador = findViewById(R.id.edtNomeJogador);
        btnComecar = findViewById(R.id.btnComecar);

        btnComecar.setOnClickListener(v -> iniciar());
    }

    private void iniciar() {
        String nome = controller.validarNome(
                edtNomeJogador.getText().toString()
        );

        if (nome == null) {
            edtNomeJogador.setError("Informe seu nome");
            return;
        }

        Intent intent = new Intent(
                MainActivity.this,
                MenuActivity.class
        );

        intent.putExtra("nome_jogador", nome);

        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() chamado");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() chamado");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() chamado");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() chamado");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() chamado");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() chamado");
    }
}
