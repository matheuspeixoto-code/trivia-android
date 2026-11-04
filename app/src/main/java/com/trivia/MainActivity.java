package com.trivia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText edtNomeJogador;
    private Button btnComecar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
}
