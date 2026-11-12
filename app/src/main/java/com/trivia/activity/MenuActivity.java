
package com.trivia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "CicloDeVida_MainActivity";
    private TextView txtBoasVindas;
    private Button btnJogar;
    private Button btnHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        txtBoasVindas = findViewById(R.id.txtBoasVindas);
        btnJogar = findViewById(R.id.btnJogar);
        btnHistorico = findViewById(R.id.btnHistorico);

        String nomeJogador = getIntent().getStringExtra("nome_jogador");

        txtBoasVindas.setText("Bem-vindo, " + nomeJogador + "!");


        btnJogar.setOnClickListener(view ->{
            Intent intent = new Intent(
                    MenuActivity.this,
                    ConfiguracaoActivity.class
                    );

            intent.putExtra("nome_jogador", getIntent().getStringExtra("nome_jogador"));

            startActivity(intent);
        });
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
