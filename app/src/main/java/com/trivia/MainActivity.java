package com.trivia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText edtNomeJogador;
    private Button btnComecar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CICLO_ACTIVITY", "onCreate executado");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNomeJogador = findViewById(R.id.edtNomeJogador);
        btnComecar = findViewById(R.id.btnComecar);

        btnComecar.setOnClickListener(view -> {

            String nome = edtNomeJogador.getText()
                    .toString()
                    .trim();

            if (nome.isEmpty()) {
                edtNomeJogador.setError("Informe seu nome");
                return;
            }

            Toast.makeText(
                    this,
                    "Bem-vindo, " + nome,
                    Toast.LENGTH_SHORT
            ).show();



        });
    }

    @Override
    protected void onStart() {
        Log.d("CICLO_ACTIVITY", "onStart executado");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("CICLO_ACTIVITY", "onResume executado");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("CICLO_ACTIVITY", "onPause executado");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("CICLO_ACTIVITY", "onStop executado");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("CICLO_ACTIVITY", "onRestart executado");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d("CICLO_ACTIVITY", "onDestroy executado");
        super.onDestroy();
    }
}
