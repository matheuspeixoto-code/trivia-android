
package com.trivia.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.trivia.R;
import com.trivia.api.ApiService;
import com.trivia.broadcast.ConexaoReceiver;
import com.trivia.database.QuestaoRepositorio;
import com.trivia.model.Categoria;
import com.trivia.model.CategoriasResponse;
import com.trivia.model.Jogador;
import com.trivia.model.Partida;
import com.trivia.model.Questao;
import com.trivia.model.QuestaoApi;
import com.trivia.model.QuestoesResponse;
import com.trivia.retrofit.RetrofitCategoria;
import com.trivia.utils.ConfiguracaoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfiguracaoActivity extends AppCompatActivity {

    private static final String TAG = "ConfiguracaoActivity";
    private static final String TIPO_QUESTAO = "multiple";

    private LinearLayout layoutCarregando;
    private LinearLayout layoutConfiguracao;
    private Spinner spDificuldade;
    private Spinner spCategoria;
    private EditText edtQuantidade;
    private SeekBar seekTempo;
    private TextView txtTempo;
    private Button btnIniciarPartida;

    private final ConexaoReceiver conexaoReceiver =
            new ConexaoReceiver();

    private final ExecutorService executorBanco =
            Executors.newSingleThreadExecutor();

    private boolean categoriasCarregadasLocalmente = false;

    private Call<CategoriasResponse> chamadaCategorias;
    private Call<QuestoesResponse> chamadaQuestoes;

    // Inicialização da tela
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configuracao);

        layoutCarregando = findViewById(R.id.layoutCarregando);
        layoutConfiguracao = findViewById(R.id.layoutConfiguracao);

        spDificuldade = findViewById(R.id.spDificuldade);
        spCategoria = findViewById(R.id.spCategoria);

        edtQuantidade = findViewById(R.id.edtQuantidade);
        seekTempo = findViewById(R.id.seekTempo);
        txtTempo = findViewById(R.id.txtTempo);

        btnIniciarPartida = findViewById(R.id.btnIniciarPartida);

        configurarTempo();
        configurarDificuldade();
        carregarCategorias();

        btnIniciarPartida.setOnClickListener(
                v -> obterConfiguracao()
        );
    }

    // Registra o receptor de conectividade
    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filtro = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION
        );

        registerReceiver(conexaoReceiver, filtro);

        Log.d(TAG, "onStart() chamado");
    }

    // Remove o receptor quando a tela é interrompida
    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(conexaoReceiver);

        Log.d(TAG, "onStop() chamado");
    }

    // Libera os recursos utilizados
    @Override
    protected void onDestroy() {

        if (chamadaCategorias != null) {
            chamadaCategorias.cancel();
        }

        if (chamadaQuestoes != null) {
            chamadaQuestoes.cancel();
        }

        // Permite concluir as tarefas já enfileiradas
        executorBanco.shutdown();

        super.onDestroy();
    }

    // Configuração dos níveis de dificuldade
    private void configurarDificuldade() {

        String[] dificuldades = {
                "Any Difficult",
                "Easy",
                "Medium",
                "Hard"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                dificuldades
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spDificuldade.setAdapter(adapter);
    }

    // Configuração do tempo por pergunta
    private void configurarTempo() {

        seekTempo.setProgress(20);

        txtTempo.setText(
                (seekTempo.getProgress() + 10) + " segundos"
        );

        seekTempo.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        int tempo = progress + 10;

                        txtTempo.setText(
                                tempo + " segundos"
                        );
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar) {
                    }
                }
        );
    }

    // Exibe categorias na tela de configuração
    private void exibirCategorias(
            List<Categoria> categorias,
            boolean locais) {

        if (isFinishing() || isDestroyed()) {
            return;
        }

        categoriasCarregadasLocalmente = locais;

        ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categorias
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spCategoria.setAdapter(adapter);

        layoutCarregando.setVisibility(View.GONE);
        layoutConfiguracao.setVisibility(View.VISIBLE);
    }

    // Busca categorias pela API
    private void carregarCategorias() {

        ApiService api = RetrofitCategoria.getApiService();

        chamadaCategorias = api.buscarCategorias();

        chamadaCategorias.enqueue(
                new Callback<CategoriasResponse>() {

                    @Override
                    public void onResponse(
                            Call<CategoriasResponse> call,
                            Response<CategoriasResponse> response) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getCategorias() != null) {

                            List<Categoria> categorias =
                                    new ArrayList<>();

                            categorias.add(
                                    new Categoria(
                                            0,
                                            "Todas as categorias"
                                    )
                            );

                            categorias.addAll(
                                    response.body().getCategorias()
                            );

                            exibirCategorias(categorias, false);

                        } else {

                            Log.w(
                                    TAG,
                                    "Falha ao carregar categorias online: HTTP "
                                            + response.code()
                            );

                            carregarCategoriasOffline();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<CategoriasResponse> call,
                            Throwable t) {

                        if (isFinishing()
                                || isDestroyed()
                                || call.isCanceled()) {
                            return;
                        }

                        Log.w(
                                TAG,
                                "Sem acesso às categorias online. Tentando o banco local.",
                                t
                        );

                        carregarCategoriasOffline();
                    }
                }
        );
    }

    // Recupera categorias salvas no SQLite
    private void carregarCategoriasOffline() {

        executorBanco.execute(() -> {

            List<Categoria> categorias;

            try {

                QuestaoRepositorio repositorio =
                        new QuestaoRepositorio(
                                getApplicationContext()
                        );

                categorias =
                        repositorio.listarCategoriasOffline();

            } catch (Exception e) {

                Log.e(
                        TAG,
                        "Não foi possível consultar as categorias locais.",
                        e
                );

                categorias = new ArrayList<>();

                categorias.add(
                        new Categoria(
                                0,
                                "Todas as categorias"
                        )
                );
            }

            final List<Categoria> categoriasDisponiveis =
                    categorias;

            runOnUiThread(() -> {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                exibirCategorias(
                        categoriasDisponiveis,
                        true
                );

                String mensagem =
                        categoriasDisponiveis.size() > 1
                                ? "Modo offline: categorias salvas carregadas."
                                : "Sem internet. Use perguntas salvas, se houver.";

                Toast.makeText(
                        this,
                        mensagem,
                        Toast.LENGTH_LONG
                ).show();
            });
        });
    }

    // Obtém e valida as configurações da partida
    private void obterConfiguracao() {

        Categoria categoriaSelecionada =
                (Categoria) spCategoria.getSelectedItem();

        if (categoriaSelecionada == null) {

            Toast.makeText(
                    this,
                    "Aguarde o carregamento das categorias.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String textoQuantidade =
                edtQuantidade.getText().toString().trim();

        if (textoQuantidade.isEmpty()) {

            edtQuantidade.setError(
                    "Informe a quantidade de questões."
            );

            return;
        }

        int quantidade;

        try {

            quantidade = Integer.parseInt(
                    textoQuantidade
            );

        } catch (NumberFormatException e) {

            edtQuantidade.setError(
                    "Quantidade inválida."
            );

            return;
        }

        if (quantidade < 1 || quantidade > 50) {

            edtQuantidade.setError(
                    "Informe um valor entre 1 e 50."
            );

            return;
        }

        String dificuldade =
                ConfiguracaoUtils.obterDificuldade(
                        spDificuldade.getSelectedItemPosition()
                );

        int tempo = seekTempo.getProgress() + 10;

        String nomeCategoria =
                categoriaSelecionada.getNome();

        Integer categoriaApiId =
                categoriaSelecionada.getId() == 0
                        ? null
                        : categoriaSelecionada.getId();

        if (categoriasCarregadasLocalmente) {

            // IDs do SQLite não são IDs da API
            buscarQuestoesOffline(
                    quantidade,
                    nomeCategoria,
                    dificuldade,
                    tempo
            );

        } else {

            buscarQuestoesOnline(
                    quantidade,
                    categoriaApiId,
                    nomeCategoria,
                    dificuldade,
                    TIPO_QUESTAO,
                    tempo
            );
        }
    }

    // Busca questões pela API Open Trivia DB
    private void buscarQuestoesOnline(
            int quantidade,
            Integer categoriaId,
            String nomeCategoria,
            String dificuldade,
            String tipo,
            int tempo) {

        btnIniciarPartida.setEnabled(false);

        chamadaQuestoes =
                RetrofitCategoria.getApiService().buscarQuestoes(
                        quantidade,
                        categoriaId,
                        dificuldade,
                        tipo
                );

        chamadaQuestoes.enqueue(
                new Callback<QuestoesResponse>() {

                    @Override
                    public void onResponse(
                            Call<QuestoesResponse> call,
                            Response<QuestoesResponse> response) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        if (!response.isSuccessful()
                                || response.body() == null) {

                            Log.w(
                                    TAG,
                                    "Erro HTTP ao buscar questões: "
                                            + response.code()
                            );

                            buscarQuestoesOffline(
                                    quantidade,
                                    nomeCategoria,
                                    dificuldade,
                                    tempo
                            );

                            return;
                        }

                        QuestoesResponse resultado =
                                response.body();

                        if (resultado.getCodigoResposta() != 0) {

                            Log.w(
                                    TAG,
                                    obterMensagemErroApi(
                                            resultado.getCodigoResposta()
                                    ) + " Tentando cache offline."
                            );

                            buscarQuestoesOffline(
                                    quantidade,
                                    nomeCategoria,
                                    dificuldade,
                                    tempo
                            );

                            return;
                        }

                        if (resultado.getQuestoes() == null
                                || resultado.getQuestoes().isEmpty()) {

                            buscarQuestoesOffline(
                                    quantidade,
                                    nomeCategoria,
                                    dificuldade,
                                    tempo
                            );

                            return;
                        }

                        List<Questao> questoesJogo =
                                new ArrayList<>();

                        for (QuestaoApi questaoApi :
                                resultado.getQuestoes()) {

                            if (questaoApi == null
                                    || questaoApi.getEnunciado() == null
                                    || questaoApi.getRespostaCorreta() == null
                                    || questaoApi.getRespostasIncorretas() == null
                                    || questaoApi.getRespostasIncorretas().size() != 3) {

                                continue;
                            }

                            Questao questao = new Questao(
                                    questaoApi.getCategory(),
                                    questaoApi.getDifficulty(),
                                    questaoApi.getEnunciado(),
                                    questaoApi.getRespostaCorreta(),
                                    questaoApi.getRespostasIncorretas()
                            );

                            questoesJogo.add(questao);
                        }

                        if (questoesJogo.size() < quantidade) {

                            Log.w(
                                    TAG,
                                    "A API retornou menos questões válidas que o solicitado."
                            );

                            buscarQuestoesOffline(
                                    quantidade,
                                    nomeCategoria,
                                    dificuldade,
                                    tempo
                            );

                            return;
                        }

                        // Salva perguntas da API no SQLite
                        // usando uma thread secundária

                        List<Questao> questoesParaCache =
                                new ArrayList<>(questoesJogo);

                        executorBanco.execute(() -> {

                            try {

                                QuestaoRepositorio repositorio =
                                        new QuestaoRepositorio(
                                                getApplicationContext()
                                        );

                                repositorio.salvarQuestoesParaOffline(
                                        questoesParaCache
                                );

                                Log.d(
                                        TAG,
                                        "Perguntas online armazenadas para uso offline."
                                );

                            } catch (Exception e) {

                                Log.e(
                                        TAG,
                                        "Falha ao salvar perguntas no cache offline.",
                                        e
                                );
                            }
                        });

                        btnIniciarPartida.setEnabled(true);

                        iniciarPartida(
                                questoesJogo,
                                tempo,
                                false
                        );
                    }

                    @Override
                    public void onFailure(
                            Call<QuestoesResponse> call,
                            Throwable t) {

                        if (isFinishing()
                                || isDestroyed()
                                || call.isCanceled()) {

                            return;
                        }

                        Log.w(
                                TAG,
                                "Falha na conexão. Tentando perguntas locais.",
                                t
                        );

                        buscarQuestoesOffline(
                                quantidade,
                                nomeCategoria,
                                dificuldade,
                                tempo
                        );
                    }
                }
        );
    }

    // Busca questões já armazenadas no SQLite
    private void buscarQuestoesOffline(
            int quantidade,
            String nomeCategoria,
            String dificuldade,
            int tempo) {

        btnIniciarPartida.setEnabled(false);

        executorBanco.execute(() -> {

            List<Questao> questoes =
                    new ArrayList<>();

            try {

                QuestaoRepositorio repositorio =
                        new QuestaoRepositorio(
                                getApplicationContext()
                        );

                questoes =
                        repositorio.buscarQuestoesOffline(
                                quantidade,
                                nomeCategoria,
                                dificuldade
                        );

            } catch (Exception e) {

                Log.e(
                        TAG,
                        "Erro ao buscar perguntas offline.",
                        e
                );
            }

            final List<Questao> perguntasDisponiveis =
                    questoes;

            runOnUiThread(() -> {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                btnIniciarPartida.setEnabled(true);

                if (perguntasDisponiveis.size() < quantidade) {

                    Toast.makeText(
                            this,
                            "Sem conexão ou perguntas insuficientes no cache "
                                    + "para essa categoria/dificuldade ("
                                    + perguntasDisponiveis.size()
                                    + "/"
                                    + quantidade
                                    + ").",
                            Toast.LENGTH_LONG
                    ).show();

                    return;
                }

                iniciarPartida(
                        perguntasDisponiveis,
                        tempo,
                        true
                );
            });
        });
    }

    // Inicia a partida com as questões recebidas
    private void iniciarPartida(
            List<Questao> questoes,
            int tempo,
            boolean offline) {

        String nomeJogador =
                getIntent().getStringExtra("nome_jogador");

        Jogador jogador =
                new Jogador(0, nomeJogador);

        Partida partida = new Partida(
                0,
                jogador,
                questoes,
                0,
                0,
                0,
                tempo
        );

        Intent intent = new Intent(
                this,
                JogoActivity.class
        );

        intent.putExtra("partida", partida);

        startActivity(intent);

        Toast.makeText(
                this,
                offline
                        ? "Partida offline iniciada!"
                        : "Questões carregadas com sucesso!",
                Toast.LENGTH_SHORT
        ).show();
    }

    // Mensagens para erros retornados pela API
    private String obterMensagemErroApi(int codigo) {

        switch (codigo) {

            case 1:
                return "A API não tem questões suficientes nessa configuração.";

            case 2:
                return "Parâmetros inválidos enviados à API.";

            case 3:
                return "Token de sessão não encontrado.";

            case 4:
                return "Token de sessão esgotado.";

            case 5:
                return "Limite de requisições da API atingido.";

            default:
                return "Erro desconhecido ao buscar questões.";
        }
    }
}
