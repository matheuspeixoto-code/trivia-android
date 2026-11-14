
package com.trivia.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;
import android.widget.Toast;

public class ConexaoReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network rede = cm.getActiveNetwork();

        NetworkCapabilities capacidades = cm.getNetworkCapabilities(rede);

        boolean conectado = capacidades != null &&
                capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                &&
                capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);

        String mensagem = conectado ? "Internet disponível!" : "Sem conexão com a internet!";

        Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();

        Log.d("ConexaoReceiver", "Evento de conexão recebido!");
    }
}
